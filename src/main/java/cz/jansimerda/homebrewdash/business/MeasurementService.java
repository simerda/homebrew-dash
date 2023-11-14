package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.exception.exposed.ConflictException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import cz.jansimerda.homebrewdash.repository.MeasurementRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    private final HydrometerRepository hydrometerRepository;

    private final BeerRepository beerRepository;

    public MeasurementService(
            MeasurementRepository measurementRepository,
            HydrometerRepository hydrometerRepository,
            BeerRepository beerRepository
    ) {
        this.measurementRepository = measurementRepository;
        this.hydrometerRepository = hydrometerRepository;
        this.beerRepository = beerRepository;
    }

    /**
     * Stores a new Measurement
     *
     * @param measurement measurement entity to be stored
     * @return created measurement
     */
    public Measurement create(Measurement measurement, String token) {
        Hydrometer hydrometer = hydrometerRepository.getFirstByToken(token)
                .orElseThrow(() -> new AccessDeniedException("Access to hydrometer forbidden"));

        Beer assignedBeer = hydrometer.getAssignedBeer()
                .orElseThrow(() -> new ConflictException(
                        "Cannot store the measurement as the hydrometer doesn't have a beer assigned"
                ));

        // if gravity is outside sensible range, we'll hide the data point
        boolean unreliableData = measurement.getSpecificGravity().compareTo(new BigDecimal(1)) < 0
                || measurement.getSpecificGravity().compareTo(new BigDecimal("1.3")) > 0;

        measurement.setIsHidden(!hydrometer.isActive() || unreliableData);
        measurement.setHydrometer(hydrometer);
        measurement.setBeer(assignedBeer);

        return measurementRepository.save(measurement);
    }

    /**
     * Fetches a measurement
     *
     * @param id id of measurement to be fetched
     * @return fetched measurement
     */
    public Optional<Measurement> readById(UUID id) {
        Optional<Measurement> measurement = measurementRepository.findById(id);
        measurement.ifPresent(this::ensureIsAccessible);

        return measurement;
    }

    /**
     * Fetches all measurements current user can access from the repository
     *
     * @return list of measurements
     */
    public List<Measurement> readAll() {
        CustomUserDetails userDetails = AuthenticationHelper.getUserDetails();
        if (userDetails.isAdmin()) {
            return measurementRepository.findAll();
        }

        return measurementRepository.findByBeerCreatedById(userDetails.getId());
    }

    /**
     * Attempts to update existing Measurement.
     *
     * @param id     id of the measurement
     * @param beerId id of the beer to be assigned
     * @param hidden whether this measurement data point should be hidden
     * @return updated Measurement
     * @throws EntityNotFoundException if Measurement or Beer cannot be found
     * @throws AccessDeniedException   if User doesn't have access to given Measurement or Beer
     */
    public Measurement update(UUID id, UUID beerId, boolean hidden) throws EntityNotFoundException, AccessDeniedException {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Measurement.class, id));
        ensureIsAccessible(measurement);

        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new EntityNotFoundException(Beer.class, beerId));
        // ensure beer is accessible by measurement owner
        if (!beer.getCreatedBy().getId().equals(measurement.getBeer().getCreatedBy().getId())) {
            throw new AccessDeniedException("User doesn't have access to this beer");
        }

        measurement.setBeer(beer);
        measurement.setIsHidden(hidden);

        return measurementRepository.save(measurement);
    }

    /**
     * Attempts to delete the measurement with given ID
     *
     * @param id id of the measurement to be deleted
     * @throws EntityNotFoundException if the measurement cannot be found
     */
    public void deleteById(UUID id) throws EntityNotFoundException {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Measurement.class, id));
        ensureIsAccessible(measurement);
        measurementRepository.deleteById(id);
    }

    /**
     * Ensures that current user has access to given measurement entity, otherwise throws error
     *
     * @param entity measurement entity
     */
    private void ensureIsAccessible(Measurement entity) throws AccessDeniedException {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();

        if (!details.isAdmin() && !entity.getBeer().getCreatedBy().getId().equals(details.getId())) {
            throw new AccessDeniedException("You don't have access to this measurement");
        }
    }
}
