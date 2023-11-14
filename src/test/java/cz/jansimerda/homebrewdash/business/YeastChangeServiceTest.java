package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;
import cz.jansimerda.homebrewdash.repository.YeastChangeRepository;
import cz.jansimerda.homebrewdash.repository.YeastRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class YeastChangeServiceTest extends AbstractServiceTest {

    @Autowired
    YeastChangeService yeastChangeService;

    @MockBean
    YeastRepository yeastRepository;

    @MockBean
    YeastChangeRepository yeastChangeRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithUserAuth() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-100);

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // yeast stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUser(
                yeast.getId(),
                LocalDate.now(),
                user.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(yeastChangeRepository.save(Mockito.any(YeastChange.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    YeastChange yeastChange = (YeastChange) args[0];
                    yeastChange.setId(UUID.randomUUID());
                    yeastChange.setCreatedAt(LocalDateTime.now());
                    return yeastChange;
                });

        YeastChange created = yeastChangeService.create(change);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(change.getExpirationDate(), created.getExpirationDate());
        Assertions.assertEquals(change.getChangeGrams(), created.getChangeGrams());
        Assertions.assertEquals(user.getId(), change.getUser().getId());
        Assertions.assertEquals(yeast.getId(), change.getYeast().getId());

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1))
                .sumChangeByYeastAndUser(
                        yeast.getId(),
                        LocalDate.now(),
                        user.getId()
                );
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithAdminAuth() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-100);

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // yeast stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUser(
                yeast.getId(),
                LocalDate.now(),
                user.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(yeastChangeRepository.save(Mockito.any(YeastChange.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    YeastChange yeastChange = (YeastChange) args[0];
                    yeastChange.setId(UUID.randomUUID());
                    yeastChange.setCreatedAt(LocalDateTime.now());
                    return yeastChange;
                });

        YeastChange created = yeastChangeService.create(change);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(change.getExpirationDate(), created.getExpirationDate());
        Assertions.assertEquals(change.getChangeGrams(), created.getChangeGrams());
        Assertions.assertEquals(user.getId(), change.getUser().getId());
        Assertions.assertEquals(yeast.getId(), change.getYeast().getId());

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1))
                .sumChangeByYeastAndUser(
                        yeast.getId(),
                        LocalDate.now(),
                        user.getId()
                );
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createInsufficientStockFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-1000);

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // yeast stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUser(
                yeast.getId(),
                LocalDate.now(),
                user.getId()
        )).thenReturn(100);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> yeastChangeService.create(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1))
                .sumChangeByYeastAndUser(
                        yeast.getId(),
                        LocalDate.now(),
                        user.getId()
                );
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createYeastNotFoundFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> yeastChangeService.create(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createUserNotFoundFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> yeastChangeService.create(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createUnauthorisedUserFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getAdmin();

        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));


        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> yeastChangeService.create(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Optional<YeastChange> changeOptional = yeastChangeService.readById(change.getId());
        Assertions.assertTrue(changeOptional.isPresent());
        Assertions.assertNotNull(changeOptional.get());
        Assertions.assertEquals(change.getId(), changeOptional.get().getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Optional<YeastChange> changeOptional = yeastChangeService.readById(change.getId());
        Assertions.assertTrue(changeOptional.isPresent());
        Assertions.assertNotNull(changeOptional.get());
        Assertions.assertEquals(change.getId(), changeOptional.get().getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorised() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getAdmin();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> yeastChangeService.readById(change.getId()));

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findAllByUserId(getUser().getId())).thenReturn(List.of(change));

        List<YeastChange> changes = yeastChangeService.readAll();
        Assertions.assertEquals(1, changes.size());
        Assertions.assertNotNull(changes.get(0));
        Assertions.assertEquals(change.getId(), changes.get(0).getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findAllByUserId(getUser().getId());
        Mockito.verify(yeastChangeRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findAll()).thenReturn(List.of(change));

        List<YeastChange> changes = yeastChangeService.readAll();
        Assertions.assertEquals(1, changes.size());
        Assertions.assertNotNull(changes.get(0));
        Assertions.assertEquals(change.getId(), changes.get(0).getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findAll();
        Mockito.verify(yeastChangeRepository, Mockito.never()).findAllByUserId(getUser().getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithUserAuth() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // yeast stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(yeastChangeRepository.save(change)).thenReturn(change);

        YeastChange updated = yeastChangeService.update(change);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(change.getId(), updated.getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1))
                .sumChangeByYeastAndUserExceptChangeId(
                        yeast.getId(),
                        LocalDate.now(),
                        user.getId(),
                        change.getId()
                );
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithAdminAuth() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // yeast stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(yeastChangeRepository.save(change)).thenReturn(change);

        YeastChange updated = yeastChangeService.update(change);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(change.getId(), updated.getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1))
                .sumChangeByYeastAndUserExceptChangeId(
                        yeast.getId(),
                        LocalDate.now(),
                        user.getId(),
                        change.getId()
                );
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateInsufficientStockFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-1000);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // yeast stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        )).thenReturn(100);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> yeastChangeService.update(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1))
                .sumChangeByYeastAndUserExceptChangeId(
                        yeast.getId(),
                        LocalDate.now(),
                        user.getId(),
                        change.getId()
                );
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateYeastNotFoundFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-1000);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> yeastChangeService.update(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserNotFoundFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> yeastChangeService.update(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUnauthorisedUserFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getAdmin();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> yeastChangeService.update(change));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        )).thenReturn(0);

        yeastChangeService.deleteById(change.getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        );
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).deleteById(change.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getAdmin();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        )).thenReturn(0);

        yeastChangeService.deleteById(change.getId());

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        );
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).deleteById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUnauthorisedUserFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getAdmin();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> yeastChangeService.deleteById(change.getId()));

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getAdmin();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> yeastChangeService.deleteById(change.getId()));

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdInsufficientStockFail() {
        // prepare entities
        Yeast yeast = createYeast();
        User user = getUser();

        YeastChange change = new YeastChange();
        change.setId(UUID.randomUUID());
        change.setYeast(yeast);
        change.setUser(user);
        change.setExpirationDate(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(yeastChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        )).thenReturn(-250);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> yeastChangeService.deleteById(change.getId()));

        Mockito.verify(yeastChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(yeastChangeRepository, Mockito.times(1)).sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                change.getId()
        );
    }

    /**
     * Create Yeast entity instance with given name and manufacturer name
     *
     * @return Yeast instance
     */
    private Yeast createYeast() {
        Yeast yeast = new Yeast();
        yeast.setId(UUID.randomUUID());
        yeast.setName("Test yeast name");
        yeast.setManufacturerName("Test manufacturer");
        yeast.setKind(YeastKindEnum.LIQUID);
        yeast.setType(YeastTypeEnum.LAGER);
        yeast.setCreatedBy(createUser());

        return yeast;
    }
}
