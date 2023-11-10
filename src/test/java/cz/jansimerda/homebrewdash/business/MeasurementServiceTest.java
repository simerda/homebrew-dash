package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConflictException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import cz.jansimerda.homebrewdash.repository.MeasurementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class MeasurementServiceTest extends AbstractServiceTest {

    @Autowired
    MeasurementService measurementService;

    @MockBean
    MeasurementRepository measurementRepository;

    @MockBean
    HydrometerRepository hydrometerRepository;

    @MockBean
    BeerRepository beerRepository;

    @Test
    void create() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(null, null);

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setId(UUID.randomUUID());
                    measurementToSave.setCreatedAt(LocalDateTime.now());
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(hydrometerRepository.getFirstByToken(hydrometer.getToken())).thenReturn(Optional.of(hydrometer));

        // test
        Measurement created = measurementService.create(measurement, hydrometer.getToken());
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertTrue(created.getHydrometer().isPresent());
        Assertions.assertEquals(hydrometer.getId(), created.getHydrometer().get().getId());
        Assertions.assertNotNull(created.getBeer());
        Assertions.assertEquals(hydrometer.getAssignedBeer().map(Beer::getId).orElse(null), created.getBeer().getId());
        Assertions.assertFalse(created.isHidden());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).getFirstByToken(hydrometer.getToken());
        Mockito.verify(measurementRepository, Mockito.times(1)).save(measurement);
    }

    @Test
    void createUnreliableData() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(null, null);
        // set unrealistic gravity value
        measurement.setSpecificGravity(BigDecimal.valueOf(10));

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setId(UUID.randomUUID());
                    measurementToSave.setCreatedAt(LocalDateTime.now());
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(hydrometerRepository.getFirstByToken(hydrometer.getToken())).thenReturn(Optional.of(hydrometer));

        // test
        Measurement created = measurementService.create(measurement, hydrometer.getToken());
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertTrue(created.getHydrometer().isPresent());
        Assertions.assertEquals(hydrometer.getId(), created.getHydrometer().get().getId());
        Assertions.assertNotNull(created.getBeer());
        Assertions.assertEquals(hydrometer.getAssignedBeer().map(Beer::getId).orElse(null), created.getBeer().getId());
        // ensure is hidden because of unreliable data
        Assertions.assertTrue(created.isHidden());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).getFirstByToken(hydrometer.getToken());
        Mockito.verify(measurementRepository, Mockito.times(1)).save(measurement);
    }

    @Test
    void createIncorrectTokenFail() {
        // prepare entity
        Measurement measurement = createMeasurement(null, null);

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setId(UUID.randomUUID());
                    measurementToSave.setCreatedAt(LocalDateTime.now());
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(hydrometerRepository.getFirstByToken("incorrect-token")).thenReturn(Optional.empty());

        // test
        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> measurementService.create(measurement, "incorrect-token")
        );

        Mockito.verify(hydrometerRepository, Mockito.times(1)).getFirstByToken("incorrect-token");
        Mockito.verify(measurementRepository, Mockito.never()).save(measurement);
    }

    @Test
    void createNoBeerAssignedFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setAssignedBeer(null);
        Measurement measurement = createMeasurement(null, null);

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setId(UUID.randomUUID());
                    measurementToSave.setCreatedAt(LocalDateTime.now());
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(hydrometerRepository.getFirstByToken(hydrometer.getToken())).thenReturn(Optional.of(hydrometer));

        // test
        Assertions.assertThrowsExactly(
                ConflictException.class,
                () -> measurementService.create(measurement, hydrometer.getToken())
        );

        Mockito.verify(hydrometerRepository, Mockito.times(1)).getFirstByToken(hydrometer.getToken());
        Mockito.verify(measurementRepository, Mockito.never()).save(measurement);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));

        Optional<Measurement> retrieved = measurementService.readById(measurement.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(measurement.getId(), retrieved.get().getId());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));

        Optional<Measurement> retrieved = measurementService.readById(measurement.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(measurement.getId(), retrieved.get().getId());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdNotFound() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.empty());

        Optional<Measurement> retrieved = measurementService.readById(measurement.getId());
        Assertions.assertTrue(retrieved.isEmpty());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorizedFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.getAssignedBeer().ifPresent(b -> b.setCreatedBy(getAdmin()));
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));

        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> measurementService.readById(measurement.getId())
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findByBeerCreatedById(getUser().getId())).thenReturn(List.of(measurement));

        List<Measurement> retrieved = measurementService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(measurement.getId(), retrieved.get(0).getId());

        Mockito.verify(measurementRepository, Mockito.times(1)).findByBeerCreatedById(getUser().getId());
        Mockito.verify(measurementRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findAll()).thenReturn(List.of(measurement));

        List<Measurement> retrieved = measurementService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(measurement.getId(), retrieved.get(0).getId());

        Mockito.verify(measurementRepository, Mockito.never()).findByBeerCreatedById(getUser().getId());
        Mockito.verify(measurementRepository, Mockito.times(1)).findAll();
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() {
        // prepare entity
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("NEIPA");
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.setCreatedAt(LocalDateTime.now().minusDays(1));

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        // test
        Measurement updated = measurementService.update(measurement.getId(), beer.getId(), true);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertEquals(measurement.getId(), updated.getId());
        Assertions.assertNotNull(updated.getBeer());
        Assertions.assertEquals(beer.getId(), updated.getBeer().getId());
        Assertions.assertTrue(updated.isHidden());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(measurementRepository, Mockito.times(1)).save(measurement);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() {
        // prepare entity
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("NEIPA");
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.setCreatedAt(LocalDateTime.now().minusDays(1));

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        // test
        Measurement updated = measurementService.update(measurement.getId(), beer.getId(), true);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertEquals(measurement.getId(), updated.getId());
        Assertions.assertNotNull(updated.getBeer());
        Assertions.assertEquals(beer.getId(), updated.getBeer().getId());
        Assertions.assertTrue(updated.isHidden());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(measurementRepository, Mockito.times(1)).save(measurement);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateMeasurementNotFoundFail() {
        // prepare entity
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("NEIPA");
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.setCreatedAt(LocalDateTime.now().minusDays(1));

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        // test
        Assertions.assertThrowsExactly(
                EntityNotFoundException.class,
                () -> measurementService.update(measurement.getId(), beer.getId(), true)
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(beerRepository, Mockito.never()).findById(beer.getId());
        Mockito.verify(measurementRepository, Mockito.never()).save(measurement);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateBeerNotFoundFail() {
        // prepare entity
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("NEIPA");
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.setCreatedAt(LocalDateTime.now().minusDays(1));

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.empty());

        // test
        Assertions.assertThrowsExactly(
                EntityNotFoundException.class,
                () -> measurementService.update(measurement.getId(), beer.getId(), true)
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(measurementRepository, Mockito.never()).save(measurement);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserUnauthorisedFail() {
        // prepare entity
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("NEIPA");
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.setCreatedAt(LocalDateTime.now().minusDays(1));
        measurement.getBeer().setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        // test
        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> measurementService.update(measurement.getId(), beer.getId(), true)
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(beerRepository, Mockito.never()).findById(beer.getId());
        Mockito.verify(measurementRepository, Mockito.never()).save(measurement);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateBeerInaccessibleFail() {
        // prepare entity
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("NEIPA");
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getAdmin());

        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.setCreatedAt(LocalDateTime.now().minusDays(1));

        // mock repository calls
        Mockito.when(measurementRepository.save(Mockito.any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Measurement measurementToSave = (Measurement) args[0];
                    measurementToSave.setUpdatedAt(LocalDateTime.now());
                    return measurementToSave;
                });

        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        // test
        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> measurementService.update(measurement.getId(), beer.getId(), true)
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(measurementRepository, Mockito.never()).save(measurement);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));

        measurementService.deleteById(measurement.getId());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(measurementRepository, Mockito.times(1)).deleteById(measurement.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));

        measurementService.deleteById(measurement.getId());

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(measurementRepository, Mockito.times(1)).deleteById(measurement.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(
                EntityNotFoundException.class,
                () -> measurementService.deleteById(measurement.getId())
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(measurementRepository, Mockito.never()).deleteById(measurement.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUserUnauthorizedFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        Measurement measurement = createMeasurement(hydrometer, hydrometer.getAssignedBeer().orElse(null));
        measurement.setId(UUID.randomUUID());
        measurement.getBeer().setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(measurementRepository.findById(measurement.getId())).thenReturn(Optional.of(measurement));

        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> measurementService.deleteById(measurement.getId())
        );

        Mockito.verify(measurementRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.verify(measurementRepository, Mockito.never()).deleteById(measurement.getId());
    }

    /**
     * Helper method to create a dummy hydrometer instance
     *
     * @return created hydrometer and stored
     */
    private Hydrometer createHydrometer() {
        Beer beer = new Beer();
        beer.setId(UUID.randomUUID());
        beer.setName("Berliner Weiss-bier");
        beer.setState(BrewStateEnum.PLANNING);
        beer.setCreatedBy(getUser());
        beer.setUpdatedAt(LocalDateTime.now());
        beer.setCreatedAt(LocalDateTime.now());

        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setName("iSpindel");
        hydrometer.setAssignedBeer(null);
        hydrometer.setIsActive(true);
        hydrometer.setAssignedBeer(beer);
        hydrometer.setCreatedBy(getUser());

        return hydrometer;
    }

    /**
     * Helper method to create a dummy measurement instance
     *
     * @param hydrometer hydrometer to assign the measurement to
     * @param beer       beer to assign the measurement to
     * @return created measurement
     */
    private Measurement createMeasurement(Hydrometer hydrometer, Beer beer) {
        Measurement measurement = new Measurement();
        measurement.setAngle(BigDecimal.valueOf(65.32));
        measurement.setTemperature(BigDecimal.valueOf(18.93));
        measurement.setBattery(BigDecimal.valueOf(2.11));
        measurement.setSpecificGravity(BigDecimal.valueOf(1.089));
        measurement.setInterval(300);
        measurement.setRssi(-73);
        measurement.setHydrometer(hydrometer);
        measurement.setBeer(beer);
        measurement.setIsHidden(false);

        return measurement;
    }
}
