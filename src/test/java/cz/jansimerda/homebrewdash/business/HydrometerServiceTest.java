package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class HydrometerServiceTest extends AbstractServiceTest {

    @Autowired
    HydrometerService hydrometerService;

    @MockBean
    HydrometerRepository hydrometerRepository;

    @MockBean
    BeerRepository beerRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createNoBeer() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setId(UUID.randomUUID());
                    hydrometerToSave.setCreatedAt(LocalDateTime.now());
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Hydrometer created = hydrometerService.create(hydrometer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertTrue(created.getAssignedBeer().isEmpty());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertNotNull(created.getToken());
        Assertions.assertEquals(Hydrometer.TOKEN_LENGTH, created.getToken().length());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithBeer() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        UUID beerId = UUID.randomUUID();
        Beer assignedBeer = new Beer();
        assignedBeer.setId(beerId);
        assignedBeer.setCreatedBy(getUser());
        hydrometer.setAssignedBeer(assignedBeer);

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setId(UUID.randomUUID());
                    hydrometerToSave.setCreatedAt(LocalDateTime.now());
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(assignedBeer));

        Hydrometer created = hydrometerService.create(hydrometer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertTrue(created.getAssignedBeer().isPresent());
        Assertions.assertEquals(beerId, created.getAssignedBeer().get().getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertNotNull(created.getToken());
        Assertions.assertEquals(Hydrometer.TOKEN_LENGTH, created.getToken().length());

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.times(1)).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithBeerNotExistsFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        UUID beerId = UUID.randomUUID();
        Beer assignedBeer = new Beer();
        assignedBeer.setId(beerId);
        assignedBeer.setCreatedBy(getUser());
        hydrometer.setAssignedBeer(assignedBeer);

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setId(UUID.randomUUID());
                    hydrometerToSave.setCreatedAt(LocalDateTime.now());
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hydrometerService.create(hydrometer));

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.never()).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithBeerUnauthorisedFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        UUID beerId = UUID.randomUUID();
        Beer assignedBeer = new Beer();
        assignedBeer.setId(beerId);
        assignedBeer.setCreatedBy(getAdmin());
        hydrometer.setAssignedBeer(assignedBeer);

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setId(UUID.randomUUID());
                    hydrometerToSave.setCreatedAt(LocalDateTime.now());
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(assignedBeer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hydrometerService.create(hydrometer));

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.never()).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Optional<Hydrometer> retrieved = hydrometerService.readById(hydrometer.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(hydrometer.getId(), retrieved.get().getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Optional<Hydrometer> retrieved = hydrometerService.readById(hydrometer.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(hydrometer.getId(), retrieved.get().getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorizedFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hydrometerService.readById(hydrometer.getId()));

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdNotFoundFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.empty());

        Optional<Hydrometer> retrieved = hydrometerService.readById(hydrometer.getId());
        Assertions.assertTrue(retrieved.isEmpty());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findByCreatedById(getUser().getId())).thenReturn(List.of(hydrometer));

        List<Hydrometer> retrieved = hydrometerService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(hydrometer.getId(), retrieved.get(0).getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findByCreatedById(getUser().getId());
        Mockito.verify(hydrometerRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findAll()).thenReturn(List.of(hydrometer));

        List<Hydrometer> retrieved = hydrometerService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(hydrometer.getId(), retrieved.get(0).getId());

        Mockito.verify(hydrometerRepository, Mockito.never()).findByCreatedById(getUser().getId());
        Mockito.verify(hydrometerRepository, Mockito.times(1)).findAll();
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() {
        // prepare entity
        UUID beerId = UUID.randomUUID();
        Beer beer = new Beer();
        beer.setId(beerId);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedAt(null);
        hydrometer.setCreatedBy(null);
        hydrometer.setName("Tilt");
        hydrometer.setIsActive(false);
        hydrometer.setAssignedBeer(beer);

        Hydrometer existing = createHydrometer();
        existing.setId(hydrometer.getId());
        existing.setId(hydrometer.getId());
        existing.setAssignedBeer(null);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(existing));
        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(beer));

        Hydrometer updated = hydrometerService.update(hydrometer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Tilt", updated.getName());
        Assertions.assertTrue(updated.getAssignedBeer().isPresent());
        Assertions.assertEquals(beerId, updated.getAssignedBeer().get().getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.times(1)).save(hydrometer);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() {
        // prepare entity
        UUID beerId = UUID.randomUUID();
        Beer beer = new Beer();
        beer.setId(beerId);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedAt(null);
        hydrometer.setCreatedBy(null);
        hydrometer.setName("Tilt");
        hydrometer.setIsActive(false);
        hydrometer.setAssignedBeer(beer);

        Hydrometer existing = createHydrometer();
        existing.setId(hydrometer.getId());
        existing.setId(hydrometer.getId());
        existing.setAssignedBeer(null);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(existing));
        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(beer));

        Hydrometer updated = hydrometerService.update(hydrometer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Tilt", updated.getName());
        Assertions.assertTrue(updated.getAssignedBeer().isPresent());
        Assertions.assertEquals(beerId, updated.getAssignedBeer().get().getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.times(1)).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateRemoveBeer() {
        // prepare entity
        UUID beerId = UUID.randomUUID();
        Beer beer = new Beer();
        beer.setId(beerId);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedAt(null);
        hydrometer.setCreatedBy(null);
        hydrometer.setName("Tilt");
        hydrometer.setIsActive(false);
        hydrometer.setAssignedBeer(null);

        Hydrometer existing = createHydrometer();
        existing.setId(hydrometer.getId());
        existing.setId(hydrometer.getId());
        existing.setAssignedBeer(null);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());
        existing.setAssignedBeer(beer);

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(existing));
        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(beer));

        Hydrometer updated = hydrometerService.update(hydrometer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertFalse(updated.isActive());
        Assertions.assertEquals("Tilt", updated.getName());
        Assertions.assertTrue(updated.getAssignedBeer().isEmpty());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(beerRepository, Mockito.never()).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.times(1)).save(hydrometer);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateBeerInaccessibleFail() {
        // prepare entity
        UUID beerId = UUID.randomUUID();
        Beer beer = new Beer();
        beer.setId(beerId);
        beer.setCreatedBy(getAdmin());

        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedAt(null);
        hydrometer.setCreatedBy(null);
        hydrometer.setName("Tilt");
        hydrometer.setIsActive(false);
        hydrometer.setAssignedBeer(beer);

        Hydrometer existing = createHydrometer();
        existing.setId(hydrometer.getId());
        existing.setId(hydrometer.getId());
        existing.setAssignedBeer(null);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(existing));
        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(beer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hydrometerService.update(hydrometer));

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.never()).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateNotFoundFail() {
        // prepare entity
        UUID beerId = UUID.randomUUID();
        Beer beer = new Beer();
        beer.setId(beerId);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedAt(null);
        hydrometer.setCreatedBy(null);
        hydrometer.setName("Tilt");
        hydrometer.setIsActive(false);
        hydrometer.setAssignedBeer(beer);

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(beer));

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hydrometerService.update(hydrometer));

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(beerRepository, Mockito.never()).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.never()).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUnauthorized() {
        // prepare entity
        UUID beerId = UUID.randomUUID();
        Beer beer = new Beer();
        beer.setId(beerId);
        beer.setCreatedBy(getUser());

        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedAt(null);
        hydrometer.setCreatedBy(null);
        hydrometer.setName("Tilt");
        hydrometer.setIsActive(false);
        hydrometer.setAssignedBeer(beer);

        Hydrometer existing = createHydrometer();
        existing.setId(hydrometer.getId());
        existing.setId(hydrometer.getId());
        existing.setAssignedBeer(null);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(hydrometerRepository.save(Mockito.any(Hydrometer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hydrometer hydrometerToSave = (Hydrometer) args[0];
                    hydrometerToSave.setUpdatedAt(LocalDateTime.now());
                    return hydrometerToSave;
                });

        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(existing));
        Mockito.when(beerRepository.findById(beerId)).thenReturn(Optional.of(beer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hydrometerService.update(hydrometer));

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(beerRepository, Mockito.never()).findById(beerId);
        Mockito.verify(hydrometerRepository, Mockito.never()).save(hydrometer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        hydrometerService.deleteById(hydrometer.getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(hydrometerRepository, Mockito.times(1)).deleteById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        hydrometerService.deleteById(hydrometer.getId());

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(hydrometerRepository, Mockito.times(1)).deleteById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(
                EntityNotFoundException.class,
                () -> hydrometerService.deleteById(hydrometer.getId())
        );

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(hydrometerRepository, Mockito.never()).deleteById(hydrometer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUserUnauthorizedFail() {
        // prepare entity
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setId(UUID.randomUUID());
        hydrometer.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(hydrometerRepository.findById(hydrometer.getId())).thenReturn(Optional.of(hydrometer));

        Assertions.assertThrowsExactly(
                AccessDeniedException.class,
                () -> hydrometerService.deleteById(hydrometer.getId())
        );

        Mockito.verify(hydrometerRepository, Mockito.times(1)).findById(hydrometer.getId());
        Mockito.verify(hydrometerRepository, Mockito.never()).deleteById(hydrometer.getId());
    }

    /**
     * Helper method to create a dummy hydrometer instance
     *
     * @return created hydrometer
     */
    private Hydrometer createHydrometer() {
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setName("iSpindel");
        hydrometer.setAssignedBeer(null);
        hydrometer.setIsActive(true);

        return hydrometer;
    }
}
