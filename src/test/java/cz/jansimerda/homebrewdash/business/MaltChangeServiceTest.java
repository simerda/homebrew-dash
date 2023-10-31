package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.MaltChangeRepository;
import cz.jansimerda.homebrewdash.repository.MaltRepository;
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
class MaltChangeServiceTest extends AbstractServiceTest {

    @Autowired
    MaltChangeService maltChangeService;

    @MockBean
    MaltRepository maltRepository;

    @MockBean
    MaltChangeRepository maltChangeRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithUserAuth() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-100);

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // malt stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserId(malt.getId(), user.getId())).thenReturn(100);

        // mock save
        Mockito.when(maltChangeRepository.save(Mockito.any(MaltChange.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    MaltChange maltChange = (MaltChange) args[0];
                    maltChange.setId(UUID.randomUUID());
                    maltChange.setCreatedAt(LocalDateTime.now());
                    return maltChange;
                });

        MaltChange created = maltChangeService.create(change);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(user.getId(), change.getUser().getId());
        Assertions.assertEquals(malt.getId(), change.getMalt().getId());
        Assertions.assertEquals(change.getColorEbc(), created.getColorEbc());

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1))
                .sumChangeByMaltIdAndUserId(malt.getId(), user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithAdminAuth() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-100);

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // malt stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserId(malt.getId(), user.getId())).thenReturn(100);

        // mock save
        Mockito.when(maltChangeRepository.save(Mockito.any(MaltChange.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    MaltChange maltChange = (MaltChange) args[0];
                    maltChange.setId(UUID.randomUUID());
                    maltChange.setCreatedAt(LocalDateTime.now());
                    return maltChange;
                });

        MaltChange created = maltChangeService.create(change);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(user.getId(), change.getUser().getId());
        Assertions.assertEquals(malt.getId(), change.getMalt().getId());
        Assertions.assertEquals(change.getColorEbc(), created.getColorEbc());

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1))
                .sumChangeByMaltIdAndUserId(malt.getId(), user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createInsufficientStockFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-1000);

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // malt stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserId(malt.getId(), user.getId())).thenReturn(100);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> maltChangeService.create(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1))
                .sumChangeByMaltIdAndUserId(malt.getId(), user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createMaltNotFoundFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(null);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> maltChangeService.create(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createUserNotFoundFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(null);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> maltChangeService.create(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createUnauthorisedUserFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getAdmin();

        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(null);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));


        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> maltChangeService.create(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Optional<MaltChange> changeOptional = maltChangeService.readById(change.getId());
        Assertions.assertTrue(changeOptional.isPresent());
        Assertions.assertNotNull(changeOptional.get());
        Assertions.assertEquals(change.getId(), changeOptional.get().getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Optional<MaltChange> changeOptional = maltChangeService.readById(change.getId());
        Assertions.assertTrue(changeOptional.isPresent());
        Assertions.assertNotNull(changeOptional.get());
        Assertions.assertEquals(change.getId(), changeOptional.get().getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorised() {
        // prepare entities
        Malt malt = createMalt();
        User user = getAdmin();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> maltChangeService.readById(change.getId()));

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findAllByUserId(getUser().getId())).thenReturn(List.of(change));

        List<MaltChange> changes = maltChangeService.readAll();
        Assertions.assertEquals(1, changes.size());
        Assertions.assertNotNull(changes.get(0));
        Assertions.assertEquals(change.getId(), changes.get(0).getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findAllByUserId(getUser().getId());
        Mockito.verify(maltChangeRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findAll()).thenReturn(List.of(change));

        List<MaltChange> changes = maltChangeService.readAll();
        Assertions.assertEquals(1, changes.size());
        Assertions.assertNotNull(changes.get(0));
        Assertions.assertEquals(change.getId(), changes.get(0).getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findAll();
        Mockito.verify(maltChangeRepository, Mockito.never()).findAllByUserId(getUser().getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithUserAuth() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // malt stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId())).thenReturn(100);

        // mock save
        Mockito.when(maltChangeRepository.save(change)).thenReturn(change);

        MaltChange updated = maltChangeService.update(change);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(change.getId(), updated.getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1))
                .sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithAdminAuth() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // malt stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId())).thenReturn(100);

        // mock save
        Mockito.when(maltChangeRepository.save(change)).thenReturn(change);

        MaltChange updated = maltChangeService.update(change);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(change.getId(), updated.getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1))
                .sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateInsufficientStockFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-1000);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // malt stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId())).thenReturn(100);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> maltChangeService.update(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1))
                .sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateMaltNotFoundFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-1000);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> maltChangeService.update(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserNotFoundFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> maltChangeService.update(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUnauthorisedUserFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getAdmin();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> maltChangeService.update(change));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId())).thenReturn(0);

        maltChangeService.deleteById(change.getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).deleteById(change.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entities
        Malt malt = createMalt();
        User user = getAdmin();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId())).thenReturn(0);

        maltChangeService.deleteById(change.getId());

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).deleteById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUnauthorisedUserFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getAdmin();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> maltChangeService.deleteById(change.getId()));

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getAdmin();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> maltChangeService.deleteById(change.getId()));

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdInsufficientStockFail() {
        // prepare entities
        Malt malt = createMalt();
        User user = getUser();

        MaltChange change = new MaltChange();
        change.setId(UUID.randomUUID());
        change.setMalt(malt);
        change.setUser(user);
        change.setColorEbc(10);
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(maltChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId())).thenReturn(-250);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> maltChangeService.deleteById(change.getId()));

        Mockito.verify(maltChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(maltChangeRepository, Mockito.times(1)).sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), change.getId());
    }

    /**
     * Create Malt entity instance with given name and manufacturer name
     *
     * @return Malt instance
     */
    private Malt createMalt() {
        Malt malt = new Malt();
        malt.setId(UUID.randomUUID());
        malt.setName("Test malt name");
        malt.setManufacturerName("Test malt manufacturer name");
        malt.setCreatedBy(createUser());

        return malt;
    }
}
