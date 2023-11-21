package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.exception.exposed.ServiceUnavailableException;
import cz.jansimerda.homebrewdash.exception.internal.meross.*;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import cz.jansimerda.homebrewdash.repository.ThermostatRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class ThermostatServiceTest extends AbstractServiceTest {

    @Autowired
    ThermostatService thermostatService;

    @MockBean
    MerossService merossService;

    @MockBean
    ThermostatRepository thermostatRepository;

    @MockBean
    HydrometerRepository hydrometerRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createNoHydrometer() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Thermostat created = thermostatService.create(thermostat);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertTrue(created.getHydrometer().isEmpty());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertEquals(ThermostatStateEnum.WAITING_FOR_HYDROMETER, created.getState());
        Assertions.assertFalse(created.isPoweredOn());

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithHydrometer() throws MerossException {
        // prepare entities
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getUser());
        hydrometer.setId(UUID.randomUUID());

        Thermostat thermostat = createThermostat(hydrometer);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Thermostat created = thermostatService.create(thermostat);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertTrue(created.getHydrometer().isPresent());
        Assertions.assertEquals(hydrometer.getId(), created.getHydrometer().get().getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertEquals(ThermostatStateEnum.READY, created.getState());
        Assertions.assertFalse(created.isPoweredOn());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithHydrometerInaccessibleFail() throws MerossException {
        // prepare entities
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getAdmin());
        hydrometer.setId(UUID.randomUUID());

        Thermostat thermostat = createThermostat(hydrometer);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).save(thermostat);
        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithHydrometerNotFoundFail() throws MerossException {
        // prepare entities
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getAdmin());
        hydrometer.setId(UUID.randomUUID());

        Thermostat thermostat = createThermostat(hydrometer);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).save(thermostat);
        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createGeneralMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new GeneralMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ServiceUnavailableException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createAuthenticationMissingMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new AuthenticationMissingMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ServiceUnavailableException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createDeviceNameMissingMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new DeviceNameMissingMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ServiceUnavailableException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createInvalidCommandMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new InvalidCommandMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ServiceUnavailableException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createInvalidCredentialsMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new InvalidCredentialsMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createDeviceNotFoundMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new DeviceNotFoundMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createDeviceOfflineMerossExceptionFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setId(UUID.randomUUID());
                    thermostatToSave.setCreatedAt(LocalDateTime.now());
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });
        Mockito.doThrow(new DeviceOfflineMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> thermostatService.create(thermostat));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));

        Optional<Thermostat> retrieved = thermostatService.readById(thermostat.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(thermostat.getId(), retrieved.get().getId());

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));

        Optional<Thermostat> retrieved = thermostatService.readById(thermostat.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(thermostat.getId(), retrieved.get().getId());

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorisedFail() {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getAdmin());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> thermostatService.readById(thermostat.getId()));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdNotFoundFail() {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.empty());

        Optional<Thermostat> retrieved = thermostatService.readById(thermostat.getId());
        Assertions.assertTrue(retrieved.isEmpty());

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findByCreatedById(getUser().getId())).thenReturn(List.of(thermostat));

        List<Thermostat> retrieved = thermostatService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(thermostat.getId(), retrieved.get(0).getId());

        Mockito.verify(thermostatRepository, Mockito.times(1)).findByCreatedById(getUser().getId());
        Mockito.verify(thermostatRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findAll()).thenReturn(List.of(thermostat));

        List<Thermostat> retrieved = thermostatService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(thermostat.getId(), retrieved.get(0).getId());

        Mockito.verify(thermostatRepository, Mockito.never()).findByCreatedById(getUser().getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findAll();
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() throws MerossException {
        // prepare entity
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getUser());
        hydrometer.setId(UUID.randomUUID());

        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setDeviceName("merossPlug");
        thermostat.setEmail("other@mail.com");
        thermostat.setPassword("myp4422word321*");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);
        thermostat.setIsPoweredOn(true);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Thermostat updated = thermostatService.update(thermostat);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Thermostat #2", updated.getName().orElse(null));
        Assertions.assertEquals("merossPlug", updated.getDeviceName());
        Assertions.assertEquals("other@mail.com", updated.getEmail());
        Assertions.assertEquals("myp4422word321*", updated.getPassword());
        Assertions.assertFalse(updated.isHeating());
        Assertions.assertFalse(updated.isActive());
        Assertions.assertFalse(updated.isPoweredOn()); // switched off due to credentials change
        Assertions.assertTrue(updated.getHydrometer().isPresent());
        Assertions.assertEquals(hydrometer.getId(), updated.getHydrometer().get().getId());
        Assertions.assertEquals(ThermostatStateEnum.READY, updated.getState());

        Mockito.verify(merossService, Mockito.times(1))
                .turnOff("other@mail.com", "myp4422word321*", "merossPlug");
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserWithoutThermostat() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setDeviceName("merossPlug");
        thermostat.setEmail("other@mail.com");
        thermostat.setPassword("myp4422word321*");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);

        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getUser());
        hydrometer.setId(UUID.randomUUID());

        Thermostat existing = createThermostat(hydrometer);
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Thermostat updated = thermostatService.update(thermostat);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Thermostat #2", updated.getName().orElse(null));
        Assertions.assertEquals("merossPlug", updated.getDeviceName());
        Assertions.assertEquals("other@mail.com", updated.getEmail());
        Assertions.assertEquals("myp4422word321*", updated.getPassword());
        Assertions.assertFalse(updated.isHeating());
        Assertions.assertFalse(updated.isActive());
        Assertions.assertFalse(updated.isPoweredOn()); // switched off due to credentials change
        Assertions.assertTrue(updated.getHydrometer().isEmpty());
        Assertions.assertEquals(ThermostatStateEnum.WAITING_FOR_HYDROMETER, updated.getState());

        Mockito.verify(merossService, Mockito.times(1))
                .turnOff("other@mail.com", "myp4422word321*", "merossPlug");
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithoutCredentialChanges() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));

        Thermostat updated = thermostatService.update(thermostat);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Thermostat #2", updated.getName().orElse(null));
        Assertions.assertEquals(existing.getDeviceName(), updated.getDeviceName());
        Assertions.assertEquals(existing.getEmail(), updated.getEmail());
        Assertions.assertEquals(existing.getPassword(), updated.getPassword());
        Assertions.assertFalse(updated.isHeating());
        Assertions.assertFalse(updated.isActive());
        Assertions.assertTrue(updated.isPoweredOn());
        Assertions.assertTrue(updated.getHydrometer().isEmpty());
        Assertions.assertEquals(ThermostatStateEnum.ACTIVE, updated.getState());

        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));

        Thermostat updated = thermostatService.update(thermostat);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Thermostat #2", updated.getName().orElse(null));
        Assertions.assertEquals(existing.getDeviceName(), updated.getDeviceName());
        Assertions.assertEquals(existing.getEmail(), updated.getEmail());
        Assertions.assertEquals(existing.getPassword(), updated.getPassword());
        Assertions.assertFalse(updated.isHeating());
        Assertions.assertFalse(updated.isActive());
        Assertions.assertTrue(updated.isPoweredOn());
        Assertions.assertTrue(updated.getHydrometer().isEmpty());
        Assertions.assertEquals(ThermostatStateEnum.ACTIVE, updated.getState());

        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateHydrometerInaccessibleFail() throws MerossException {
        // prepare entity
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getAdmin());
        hydrometer.setId(UUID.randomUUID());

        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setDeviceName("merossPlug");
        thermostat.setEmail("other@mail.com");
        thermostat.setPassword("myp4422word321*");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);
        thermostat.setIsPoweredOn(true);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> thermostatService.update(thermostat));

        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateHydrometerNotFoundFail() throws MerossException {
        // prepare entity
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setCreatedBy(getUser());
        hydrometer.setId(UUID.randomUUID());

        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setDeviceName("merossPlug");
        thermostat.setEmail("other@mail.com");
        thermostat.setPassword("myp4422word321*");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);
        thermostat.setIsPoweredOn(true);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> thermostatService.update(thermostat));

        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateNotFoundFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> thermostatService.update(thermostat));

        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUnauthorizedFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedAt(null);
        thermostat.setUpdatedAt(null);
        thermostat.setCreatedBy(null);
        thermostat.setName("Thermostat #2");
        thermostat.setIsHeating(false);
        thermostat.setIsActive(false);

        Thermostat existing = createThermostat();
        existing.setState(ThermostatStateEnum.ACTIVE);
        existing.setId(thermostat.getId());
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getAdmin());
        existing.setIsPoweredOn(true);

        // mock repository calls
        Mockito.when(thermostatRepository.save(Mockito.any(Thermostat.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Thermostat thermostatToSave = (Thermostat) args[0];
                    thermostatToSave.setUpdatedAt(LocalDateTime.now());
                    return thermostatToSave;
                });

        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(existing));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> thermostatService.update(thermostat));

        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).save(thermostat);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));

        thermostatService.deleteById(thermostat.getId());

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).delete(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));

        thermostatService.deleteById(thermostat.getId());

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).delete(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdServiceErrorFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));
        Mockito.doThrow(new GeneralMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        Assertions.assertThrowsExactly(
                ServiceUnavailableException.class,
                () -> thermostatService.deleteById(thermostat.getId())
        );

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.times(1)).delete(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(
                EntityNotFoundException.class,
                () -> thermostatService.deleteById(thermostat.getId())
        );

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).delete(thermostat);
        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUnauthorizedFail() throws MerossException {
        // prepare entity
        Thermostat thermostat = createThermostat();
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getAdmin());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(thermostatRepository.findById(thermostat.getId())).thenReturn(Optional.of(thermostat));

        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> thermostatService.deleteById(thermostat.getId())
        );

        Mockito.verify(thermostatRepository, Mockito.times(1)).findById(thermostat.getId());
        Mockito.verify(thermostatRepository, Mockito.never()).delete(thermostat);
        Mockito.verify(merossService, Mockito.never())
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    void scheduleThermostatsSwitchOn() throws NoSuchFieldException, IllegalAccessException, MerossException {
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("testbeer");
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setFermentationTemperatureThreshold(BigDecimal.valueOf(20));
        beer.setCreatedBy(getUser());
        beer.setBrewedAt(LocalDate.now());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setUpdatedAt(LocalDateTime.now());

        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setName("ispindel");
        hydrometer.setAssignedBeer(beer);
        hydrometer.setIsActive(true);
        hydrometer.setCreatedBy(getUser());
        hydrometer.setUpdatedAt(LocalDateTime.now());
        hydrometer.setCreatedAt(LocalDateTime.now());

        Measurement lastMeasurement = new Measurement();
        lastMeasurement.setAngle(BigDecimal.valueOf(65.32));
        lastMeasurement.setTemperature(BigDecimal.valueOf(18.93));
        lastMeasurement.setBattery(BigDecimal.valueOf(2.11));
        lastMeasurement.setSpecificGravity(BigDecimal.valueOf(1.089));
        lastMeasurement.setInterval(300);
        lastMeasurement.setRssi(-73);
        lastMeasurement.setHydrometer(hydrometer);
        lastMeasurement.setBeer(beer);
        lastMeasurement.setIsHidden(false);
        lastMeasurement.setCreatedAt(LocalDateTime.now());
        lastMeasurement.setUpdatedAt(LocalDateTime.now());

        // we will use reflection to set object to private attribute Thermostat::lastMeasurement for testing
        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setState(ThermostatStateEnum.READY);
        thermostat.setIsPoweredOn(false);
        LocalDateTime lastSuccess = LocalDateTime.now().minusSeconds(301);
        thermostat.setLastSuccessAt(lastSuccess);
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());
        Field field = Thermostat.class.getDeclaredField("lastMeasurement");
        field.setAccessible(true);
        field.set(thermostat, lastMeasurement);
        field.setAccessible(false);

        Mockito.when(thermostatRepository.findToBeSwitched(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(ThermostatStateEnum.SERVICE_ERROR),
                Mockito.eq(BrewStateEnum.FERMENTING)
        )).thenReturn(List.of(thermostat));

        thermostatService.scheduleThermostats();

        Assertions.assertTrue(thermostat.isPoweredOn());
        Assertions.assertEquals(ThermostatStateEnum.ACTIVE, thermostat.getState());
        Assertions.assertTrue(lastSuccess.isBefore(thermostat.getLastSuccessAt().orElseThrow()));

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOn(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    void scheduleThermostatsTempReadError() throws NoSuchFieldException, IllegalAccessException, MerossException {
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("testbeer");
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setFermentationTemperatureThreshold(BigDecimal.valueOf(20));
        beer.setCreatedBy(getUser());
        beer.setBrewedAt(LocalDate.now());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setUpdatedAt(LocalDateTime.now());

        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setName("ispindel");
        hydrometer.setAssignedBeer(beer);
        hydrometer.setIsActive(true);
        hydrometer.setCreatedBy(getUser());
        hydrometer.setUpdatedAt(LocalDateTime.now());
        hydrometer.setCreatedAt(LocalDateTime.now());

        Measurement lastMeasurement = new Measurement();
        lastMeasurement.setAngle(BigDecimal.valueOf(65.32));
        lastMeasurement.setTemperature(BigDecimal.valueOf(18.93));
        lastMeasurement.setBattery(BigDecimal.valueOf(2.11));
        lastMeasurement.setSpecificGravity(BigDecimal.valueOf(1.089));
        lastMeasurement.setInterval(300);
        lastMeasurement.setRssi(-73);
        lastMeasurement.setHydrometer(hydrometer);
        lastMeasurement.setBeer(beer);
        lastMeasurement.setIsHidden(false);
        lastMeasurement.setCreatedAt(LocalDateTime.now().minusHours(1));
        lastMeasurement.setUpdatedAt(LocalDateTime.now());

        // we will use reflection to set object to private attribute Thermostat::lastMeasurement for testing
        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setState(ThermostatStateEnum.READY);
        thermostat.setIsPoweredOn(true);
        LocalDateTime lastSuccess = LocalDateTime.now().minusSeconds(301);
        thermostat.setLastSuccessAt(lastSuccess);
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());
        Field field = Thermostat.class.getDeclaredField("lastMeasurement");
        field.setAccessible(true);
        field.set(thermostat, lastMeasurement);
        field.setAccessible(false);

        Mockito.when(thermostatRepository.findToBeSwitched(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(ThermostatStateEnum.SERVICE_ERROR),
                Mockito.eq(BrewStateEnum.FERMENTING)
        )).thenReturn(List.of(thermostat));

        thermostatService.scheduleThermostats();

        Assertions.assertFalse(thermostat.isPoweredOn());
        Assertions.assertEquals(ThermostatStateEnum.TEMP_READ_ERROR, thermostat.getState());

        Mockito.verify(thermostatRepository, Mockito.times(1)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    @Test
    void scheduleThermostatsServiceUnavailable() throws NoSuchFieldException, IllegalAccessException, MerossException {
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("testbeer");
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setFermentationTemperatureThreshold(BigDecimal.valueOf(20));
        beer.setCreatedBy(getUser());
        beer.setBrewedAt(LocalDate.now());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setUpdatedAt(LocalDateTime.now());

        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setName("ispindel");
        hydrometer.setAssignedBeer(beer);
        hydrometer.setIsActive(true);
        hydrometer.setCreatedBy(getUser());
        hydrometer.setUpdatedAt(LocalDateTime.now());
        hydrometer.setCreatedAt(LocalDateTime.now());

        Measurement lastMeasurement = new Measurement();
        lastMeasurement.setAngle(BigDecimal.valueOf(65.32));
        lastMeasurement.setTemperature(BigDecimal.valueOf(18.93));
        lastMeasurement.setBattery(BigDecimal.valueOf(2.11));
        lastMeasurement.setSpecificGravity(BigDecimal.valueOf(1.089));
        lastMeasurement.setInterval(300);
        lastMeasurement.setRssi(-73);
        lastMeasurement.setHydrometer(hydrometer);
        lastMeasurement.setBeer(beer);
        lastMeasurement.setIsHidden(false);
        lastMeasurement.setCreatedAt(LocalDateTime.now());
        lastMeasurement.setUpdatedAt(LocalDateTime.now());

        // we will use reflection to set object to private attribute Thermostat::lastMeasurement for testing
        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setId(UUID.randomUUID());
        thermostat.setCreatedBy(getUser());
        thermostat.setState(ThermostatStateEnum.READY);
        thermostat.setIsPoweredOn(false);
        thermostat.setLastSuccessAt(LocalDateTime.now().minusSeconds(301));
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());
        Field field = Thermostat.class.getDeclaredField("lastMeasurement");
        field.setAccessible(true);
        field.set(thermostat, lastMeasurement);
        field.setAccessible(false);

        Mockito.when(thermostatRepository.findToBeSwitched(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(ThermostatStateEnum.SERVICE_ERROR),
                Mockito.eq(BrewStateEnum.FERMENTING)
        )).thenReturn(List.of(thermostat));
        Mockito.doThrow(new GeneralMerossException("")).when(merossService)
                .turnOn(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        thermostatService.scheduleThermostats();

        Assertions.assertEquals(ThermostatStateEnum.SERVICE_ERROR, thermostat.getState());
        Assertions.assertTrue(thermostat.getLastFailAt().isPresent());

        Mockito.verify(thermostatRepository, Mockito.times(2)).save(thermostat);
        Mockito.verify(merossService, Mockito.times(1))
                .turnOn(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());
    }

    /**
     * Helper method to create a dummy thermostat instance
     *
     * @param hydrometer hydrometer to assign to the thermostat
     * @return created thermostat
     */
    private Thermostat createThermostat(Hydrometer hydrometer) {
        Thermostat thermostat = new Thermostat();
        thermostat.setName("Thermostat #1");
        thermostat.setDeviceName("Smart Plug");
        thermostat.setEmail("my@mail.com");
        thermostat.setPassword("mypass");
        thermostat.setIsHeating(true);
        thermostat.setIsActive(true);
        thermostat.setHydrometer(hydrometer);

        return thermostat;
    }

    /**
     * Helper method to create a dummy thermostat instance
     *
     * @return created thermostat
     */
    private Thermostat createThermostat() {
        return createThermostat(null);
    }
}
