package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.exception.exposed.ServiceUnavailableException;
import cz.jansimerda.homebrewdash.exception.internal.meross.DeviceNotFoundMerossException;
import cz.jansimerda.homebrewdash.exception.internal.meross.DeviceOfflineMerossException;
import cz.jansimerda.homebrewdash.exception.internal.meross.InvalidCredentialsMerossException;
import cz.jansimerda.homebrewdash.exception.internal.meross.MerossException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import cz.jansimerda.homebrewdash.repository.ThermostatRepository;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ThermostatService extends AbstractCrudService<Thermostat, UUID> {

    private final ThermostatRepository thermostatRepository;

    private final HydrometerRepository hydrometerRepository;

    private final MerossService merossService;

    private final TransactionTemplate transactionTemplate;

    /**
     * maximum granularity of the thermostat
     * 5 minutes
     */
    private final int SEC_SWITCHING_DELAY = 300;

    /**
     * minimum delay between re-trial in an erroneous state
     * 1 hour
     */
    private final int SEC_FAILURE_DELAY = 3600;

    /**
     * threshold after which status should be set to temp read error if new measurement is not received in this time
     */
    private final int SEC_TEMP_READ_FAILURE_THRESHOLD = 11 * 60;

    /**
     * difference between set and current temperature that won't trigger thermostat
     */
    private final double TEMPERATURE_LEEWAY = .25;

    protected ThermostatService(
            ThermostatRepository thermostatRepository,
            HydrometerRepository hydrometerRepository,
            MerossService merossService,
            PlatformTransactionManager transactionManager
    ) {
        super(thermostatRepository);
        this.thermostatRepository = thermostatRepository;
        this.hydrometerRepository = hydrometerRepository;
        this.merossService = merossService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Transactional
    @Override
    public Thermostat create(Thermostat entity) {
        // we'll persist the entity first, assuming the meross service won't fail, we can roll back on error
        // we wouldn't be able to roll back on the meross service if persistence failed resulting in inconsistent state
        entity.setState(
                entity.getHydrometer().isEmpty()
                        ? ThermostatStateEnum.WAITING_FOR_HYDROMETER
                        : ThermostatStateEnum.READY
        );
        entity.setIsPoweredOn(false);
        entity.setCreatedBy(AuthenticationHelper.getUser());
        fillHydrometer(entity);
        entity = super.create(entity);

        turnOffThermostat(entity.getEmail(), entity.getPassword(), entity.getDeviceName());

        return entity;
    }

    @Override
    public Optional<Thermostat> readById(UUID id) {
        Optional<Thermostat> thermostat = super.readById(id);
        thermostat.ifPresent(this::ensureIsAccessible);

        return thermostat;
    }

    @Override
    public List<Thermostat> readAll() {
        CustomUserDetails userDetails = AuthenticationHelper.getUserDetails();
        if (userDetails.isAdmin()) {
            return super.readAll();
        }

        return thermostatRepository.findByCreatedById(userDetails.getId());
    }

    @Transactional
    @Override
    public Thermostat update(Thermostat entity) throws EntityNotFoundException {
        Thermostat existing = thermostatRepository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));
        ensureIsAccessible(existing);

        // copy attributes to be preserved
        entity.setCreatedBy(existing.getCreatedBy());
        entity.setCreatedAt(existing.getCreatedAt());
        entity.setState(existing.getState());
        entity.setIsPoweredOn(existing.isPoweredOn());
        fillHydrometer(entity);

        // no change in meross credentials, service won't be invoked
        if (existing.getDeviceName().equals(entity.getDeviceName())
                && existing.getEmail().equals(entity.getEmail())
                && existing.getPassword().equals(entity.getPassword())) {
            return thermostatRepository.save(entity);
        }

        entity.setState(
                entity.getHydrometer().isEmpty()
                        ? ThermostatStateEnum.WAITING_FOR_HYDROMETER
                        : ThermostatStateEnum.READY
        );
        entity.setIsPoweredOn(false);

        // store updated entity and try to call meross afterward
        thermostatRepository.save(entity);
        turnOffThermostat(entity.getEmail(), entity.getPassword(), entity.getDeviceName());

        return entity;
    }

    @Transactional
    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Thermostat thermostat = thermostatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Thermostat.class, id));
        ensureIsAccessible(thermostat);

        thermostatRepository.delete(thermostat);
        try {
            merossService.turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        } catch (MerossException e) {
            throw new ServiceUnavailableException("Cannot delete the thermostat at the moment because of failure to turn the device off");
        }
    }

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void scheduleThermostats() {
        List<Thermostat> thermostats = thermostatRepository.findToBeSwitched(
                LocalDateTime.now().minusSeconds(SEC_SWITCHING_DELAY),
                LocalDateTime.now().minusSeconds(SEC_FAILURE_DELAY),
                ThermostatStateEnum.SERVICE_ERROR,
                BrewStateEnum.FERMENTING
        );

        for (Thermostat thermostat : thermostats) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                        switchThermostat(thermostat);
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    public void switchThermostat(Thermostat thermostat) {
        Optional<Measurement> lastMeasurement = thermostat.getLastMeasurement();
        if (
                lastMeasurement.isEmpty() || lastMeasurement.get()
                        .getCreatedAt()
                        .isBefore(LocalDateTime.now().minusSeconds(SEC_TEMP_READ_FAILURE_THRESHOLD))
        ) {
            boolean wasOn = thermostat.isPoweredOn();
            thermostat.setIsPoweredOn(false);
            thermostat.setState(ThermostatStateEnum.TEMP_READ_ERROR);
            thermostatRepository.save(thermostat);

            if (wasOn) {
                switchThermostat(thermostat, false);
            }
            return;
        }

        BigDecimal thresholdTemp = thermostat.getHydrometer()
                .orElseThrow()
                .getAssignedBeer()
                .orElseThrow()
                .getFermentationTemperatureThreshold()
                .orElseThrow();
        BigDecimal diff = lastMeasurement.get().getTemperature().subtract(thresholdTemp);
        if (diff.abs().compareTo(BigDecimal.valueOf(TEMPERATURE_LEEWAY)) <= 0) {
            return;
            // current temp within bounds
        }

        boolean shouldBeOn = diff.signum() < 0 && thermostat.isHeating();
        if (shouldBeOn == thermostat.isPoweredOn()) {
            return;
            // no need to switch
        }
        thermostat.setIsPoweredOn(shouldBeOn);
        thermostat.setState(ThermostatStateEnum.ACTIVE);
        thermostat.setLastSuccessAt(LocalDateTime.now());
        thermostatRepository.save(thermostat);

        switchThermostat(thermostat, shouldBeOn);
    }


    private void switchThermostat(Thermostat thermostat, boolean switchOn) {
        try {
            if (switchOn) {
                merossService.turnOn(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
            } else {
                merossService.turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
            }
        } catch (MerossException e) {
            thermostat.setState(ThermostatStateEnum.SERVICE_ERROR);
            thermostat.setLastFailAt(LocalDateTime.now());
            thermostatRepository.save(thermostat);
        }
    }

    /**
     * Hydrates related assigned Hydrometer entity or fails with EntityNotFoundException or AccessDeniedException
     *
     * @param thermostat thermostat instance
     * @throws EntityNotFoundException if the hydrometer cannot be found by given ID
     * @throws AccessDeniedException   if the owner of thermostat entity doesn't have access to the hydrometer entity
     */
    private void fillHydrometer(Thermostat thermostat) throws EntityNotFoundException, AccessDeniedException {
        if (thermostat.getHydrometer().isEmpty()) {
            return;
        }

        UUID id = thermostat.getHydrometer().get().getId();
        Hydrometer hydrometer = hydrometerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Cannot find Hydrometer instance with id %s to assign to thermostat".formatted(id)
        ));
        if (!hydrometer.getCreatedBy().getId().equals(thermostat.getCreatedBy().getId())) {
            throw new AccessDeniedException("You can only assign your hydrometer as a source for the thermostat");
        }

        thermostat.setHydrometer(hydrometer);
    }

    /**
     * Ensures that current user has access to given thermostat entity, otherwise throws error
     *
     * @param entity thermostat entity
     */
    private void ensureIsAccessible(Thermostat entity) throws AccessDeniedException {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();

        if (!details.isAdmin() && !entity.getCreatedBy().getId().equals(details.getId())) {
            throw new AccessDeniedException("You don't have access to this thermostat");
        }
    }

    /**
     * Delegates call to turn off thermostat to the meross service and handles exceptions
     *
     * @param email      meross authentication email
     * @param password   meross authentication password
     * @param deviceName meross device name
     */
    private void turnOffThermostat(String email, String password, String deviceName) {
        try {
            merossService.turnOff(email, password, deviceName);
        } catch (DeviceNotFoundMerossException | DeviceOfflineMerossException e) {
            throw new ConditionsNotMetException(
                    "Device with name %s could not be found".formatted(deviceName)
            );
        } catch (InvalidCredentialsMerossException e) {
            throw new ConditionsNotMetException("The provided credentials to the Meross account are incorrect");
        } catch (MerossException e) {
            throw new ServiceUnavailableException("The Meross service is not currently available");
        }
    }
}
