package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class BeerServiceTest extends AbstractServiceTest {

    @Autowired
    BeerService beerService;

    @MockBean
    BeerRepository beerRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void create() {
        // prepare entity
        Beer beer = createBeer();

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());


        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createSetVolumeBrewedBeforeFermentingFail() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.BREWING);
        beer.setVolumeBrewed(BigDecimal.valueOf(20));

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.create(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createSetVolumeRemainingBeforeVolumeBrewedFail() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setVolumeBrewed(null);
        beer.setVolumeRemaining(BigDecimal.valueOf(20));

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.create(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createSetVolumeRemainingWhenBotchedFail() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.BOTCHED);
        beer.setVolumeBrewed(BigDecimal.valueOf(12));
        beer.setVolumeRemaining(BigDecimal.valueOf(5));

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.create(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createSetVolumeRemainingOverVolumeBrewedFail() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(12));
        beer.setVolumeRemaining(BigDecimal.valueOf(20));

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.create(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createBrewedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.PLANNING);
        beer.setBrewedAt(LocalDate.now());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getBrewedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createBrewedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.BREWING);
        beer.setBrewedAt(null);

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getBrewedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createFermentedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setFermentedAt(LocalDate.now());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getFermentedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createFermentedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.MATURING);
        beer.setFermentedAt(null);

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getFermentedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createMaturedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.MATURING);
        beer.setMaturedAt(LocalDate.now());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getMaturedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createMaturedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.DONE);
        beer.setMaturedAt(null);

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getMaturedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createConsumedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(20));
        beer.setVolumeRemaining(null);
        beer.setConsumedAt(LocalDate.now());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getConsumedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);

        beer = createBeer(true);
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(20));
        beer.setVolumeRemaining(BigDecimal.valueOf(10));
        beer.setConsumedAt(LocalDate.now());
        created = beerService.create(beer);

        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getConsumedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createConsumedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(20));
        beer.setVolumeRemaining(BigDecimal.valueOf(0));
        beer.setConsumedAt(null);

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setId(UUID.randomUUID());
                    beerToSave.setCreatedAt(LocalDateTime.now());
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Beer created = beerService.create(beer);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertTrue(created.getConsumedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        Optional<Beer> retrieved = beerService.readById(beer.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(beer.getId(), retrieved.get().getId());

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        Optional<Beer> retrieved = beerService.readById(beer.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(beer.getId(), retrieved.get().getId());

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorizedFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> beerService.readById(beer.getId()));

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdNotFoundFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.empty());

        Optional<Beer> retrieved = beerService.readById(beer.getId());
        Assertions.assertTrue(retrieved.isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findByCreatedById(getUser().getId())).thenReturn(List.of(beer));

        List<Beer> retrieved = beerService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(beer.getId(), retrieved.get(0).getId());

        Mockito.verify(beerRepository, Mockito.times(1)).findByCreatedById(getUser().getId());
        Mockito.verify(beerRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findAll()).thenReturn(List.of(beer));

        List<Beer> retrieved = beerService.readAll();
        Assertions.assertEquals(1, retrieved.size());
        Assertions.assertEquals(beer.getId(), retrieved.get(0).getId());

        Mockito.verify(beerRepository, Mockito.never()).findByCreatedById(getUser().getId());
        Mockito.verify(beerRepository, Mockito.times(1)).findAll();
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setBrewedAt(null);
        beer.setFermentedAt(null);
        beer.setMaturedAt(null);
        beer.setConsumedAt(null);
        beer.setVolumeBrewed(BigDecimal.valueOf(10));
        beer.setVolumeRemaining(BigDecimal.valueOf(0));

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());
        existing.setBrewedAt(LocalDate.now().minusDays(4));
        existing.setFermentedAt(LocalDate.now().minusDays(3));
        existing.setMaturedAt(LocalDate.now().minusDays(2));
        existing.setConsumedAt(LocalDate.now().minusDays(1));

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertEquals(existing.getBrewedAt(), updated.getBrewedAt());
        Assertions.assertEquals(existing.getFermentedAt(), updated.getFermentedAt());
        Assertions.assertEquals(existing.getMaturedAt(), updated.getMaturedAt());
        Assertions.assertEquals(existing.getConsumedAt(), updated.getConsumedAt());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setBrewedAt(null);
        beer.setFermentedAt(null);
        beer.setMaturedAt(null);
        beer.setConsumedAt(null);
        beer.setVolumeBrewed(BigDecimal.valueOf(10));
        beer.setVolumeRemaining(BigDecimal.valueOf(0));

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());
        existing.setBrewedAt(LocalDate.now().minusDays(4));
        existing.setFermentedAt(LocalDate.now().minusDays(3));
        existing.setMaturedAt(LocalDate.now().minusDays(2));
        existing.setConsumedAt(LocalDate.now().minusDays(1));

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());

        // ensure date copied
        Assertions.assertEquals(existing.getBrewedAt(), updated.getBrewedAt());
        Assertions.assertEquals(existing.getFermentedAt(), updated.getFermentedAt());
        Assertions.assertEquals(existing.getMaturedAt(), updated.getMaturedAt());
        Assertions.assertEquals(existing.getConsumedAt(), updated.getConsumedAt());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateSetVolumeBrewedBeforeFermentingFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.BREWING);
        beer.setVolumeBrewed(BigDecimal.valueOf(12.5));

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.update(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateSetVolumeRemainingBeforeVolumeBrewedFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setVolumeBrewed(null);
        beer.setVolumeRemaining(BigDecimal.valueOf(20));

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.update(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateSetVolumeRemainingWhenBotchedFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.BOTCHED);
        beer.setVolumeBrewed(BigDecimal.valueOf(12));
        beer.setVolumeRemaining(BigDecimal.valueOf(5));

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.update(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateSetVolumeRemainingOverVolumeBrewedFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(12));
        beer.setVolumeRemaining(BigDecimal.valueOf(20));

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> beerService.update(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateBrewedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.PLANNING);
        beer.setBrewedAt(LocalDate.now());

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getBrewedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateBrewedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.BREWING);
        beer.setBrewedAt(null);

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getBrewedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateFermentedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.FERMENTING);
        beer.setFermentedAt(LocalDate.now());

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getFermentedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateFermentedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.MATURING);
        beer.setFermentedAt(null);

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getFermentedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateMaturedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.MATURING);
        beer.setMaturedAt(LocalDate.now());

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getMaturedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateMaturedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.DONE);
        beer.setMaturedAt(null);

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getMaturedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateConsumedAtNull() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(20));
        beer.setVolumeRemaining(null);
        beer.setConsumedAt(LocalDate.now());

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getConsumedAt().isEmpty());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateConsumedAtSet() {
        // prepare entity
        Beer beer = createBeer(true);
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.DONE);
        beer.setVolumeBrewed(BigDecimal.valueOf(20));
        beer.setVolumeRemaining(BigDecimal.valueOf(0));
        beer.setConsumedAt(null);

        Beer existing = createBeer();
        existing.setId(beer.getId());
        existing.setCreatedAt(beer.getCreatedAt());
        existing.setCreatedBy(beer.getCreatedBy());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(existing));

        Beer updated = beerService.update(beer);
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getCreatedBy());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertInstanceOf(UUID.class, updated.getId());
        Assertions.assertEquals(getUser().getId(), updated.getCreatedBy().getId());
        Assertions.assertTrue(updated.getConsumedAt().isPresent());

        Mockito.verify(beerRepository, Mockito.times(1)).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserUnauthorizedFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> beerService.update(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateNotFoundFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedAt(LocalDateTime.now());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.save(Mockito.any(Beer.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Beer beerToSave = (Beer) args[0];
                    beerToSave.setUpdatedAt(LocalDateTime.now());
                    return beerToSave;
                });

        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> beerService.update(beer));

        Mockito.verify(beerRepository, Mockito.never()).save(beer);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        beerService.deleteById(beer.getId());

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).deleteById(beer.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        beerService.deleteById(beer.getId());

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).deleteById(beer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> beerService.deleteById(beer.getId()));

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(beerRepository, Mockito.never()).deleteById(beer.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUserUnauthorizedFail() {
        // prepare entity
        Beer beer = createBeer();
        beer.setId(UUID.randomUUID());
        beer.setCreatedBy(getAdmin());

        // mock repository calls
        Mockito.when(beerRepository.findById(beer.getId())).thenReturn(Optional.of(beer));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> beerService.deleteById(beer.getId()));

        Mockito.verify(beerRepository, Mockito.times(1)).findById(beer.getId());
        Mockito.verify(beerRepository, Mockito.never()).deleteById(beer.getId());
    }

    /**
     * Helper method to create a dummy beer instance that can simple (planning phase) or complex (done)
     *
     * @param simple whether to return simple or complex record
     * @return created beer
     */
    private Beer createBeer(boolean simple) {
        Beer beer = new Beer();
        beer.setName("Citrus IPA");
        beer.setDescription("""
                    Citrus IPA: A radiant golden brew that marries the invigorating zest of freshly peeled oranges,
                    zesty grapefruits, and tangy tangerines with a harmonious burst of piney hoppy bitterness.
                    With its tantalizing aroma and refreshing, crisp mouthfeel, this beer is a perfect companion
                    for seafood, salads, grilled chicken, or spicier cuisine. The clean finish leaves a lasting
                    impression of citrus zest and a mild hoppy bitterness, inviting you to savor every sunny sip
                    of this vibrant, citrus-infused IPA.
                """);
        beer.setOriginalGravity(new BigDecimal("1.032").setScale(4, RoundingMode.HALF_UP));
        beer.setAlcoholByVolume(new BigDecimal("3.5").setScale(2, RoundingMode.HALF_UP));
        beer.setBitternessIbu(28);
        beer.setColorEbc(8);
        if (simple) {
            beer.setState(BrewStateEnum.PLANNING);
            return beer;
        }

        beer.setVolumeBrewed(new BigDecimal("25").setScale(1, RoundingMode.HALF_UP));
        beer.setVolumeRemaining(new BigDecimal("12.5").setScale(1, RoundingMode.HALF_UP));
        beer.setFinalGravityThreshold(new BigDecimal("1.005").setScale(4, RoundingMode.HALF_UP));
        beer.setFinalGravity(new BigDecimal("1.008").setScale(4, RoundingMode.HALF_UP));
        beer.setFermentationTemperatureThreshold(new BigDecimal("19.5").setScale(2, RoundingMode.HALF_UP));
        beer.setState(BrewStateEnum.DONE);

        return beer;
    }

    /**
     * Helper method to create a dummy beer instance
     *
     * @return created beer
     */
    private Beer createBeer() {
        return createBeer(false);
    }
}
