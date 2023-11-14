package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.HopChangeRepository;
import cz.jansimerda.homebrewdash.repository.HopRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class HopChangeServiceTest extends AbstractServiceTest {

    @Autowired
    HopChangeService hopChangeService;

    @MockBean
    HopRepository hopRepository;

    @MockBean
    HopChangeRepository hopChangeRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithUserAuth() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-100);

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // hop stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUser(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(hopChangeRepository.save(Mockito.any(HopChange.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    HopChange hopChange = (HopChange) args[0];
                    hopChange.setId(UUID.randomUUID());
                    hopChange.setCreatedAt(LocalDateTime.now());
                    return hopChange;
                });

        HopChange created = hopChangeService.create(change);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(change.getAlphaAcidPercentage(), created.getAlphaAcidPercentage());
        Assertions.assertEquals(change.getBetaAcidPercentage(), created.getBetaAcidPercentage());
        Assertions.assertEquals(change.getHarvestedAt(), created.getHarvestedAt());
        Assertions.assertEquals(change.getChangeGrams(), created.getChangeGrams());
        Assertions.assertEquals(user.getId(), change.getUser().getId());
        Assertions.assertEquals(hop.getId(), change.getHop().getId());

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1))
                .sumChangeByHopAndUser(
                        hop.getId(),
                        BigDecimal.valueOf(5.6),
                        BigDecimal.valueOf(2.2),
                        LocalDate.now(), user.getId()
                );
        Mockito.verify(hopChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createWithAdminAuth() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-100);

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // hop stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUser(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(hopChangeRepository.save(Mockito.any(HopChange.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    HopChange hopChange = (HopChange) args[0];
                    hopChange.setId(UUID.randomUUID());
                    hopChange.setCreatedAt(LocalDateTime.now());
                    return hopChange;
                });

        HopChange created = hopChangeService.create(change);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedAt());
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(change.getAlphaAcidPercentage(), created.getAlphaAcidPercentage());
        Assertions.assertEquals(change.getBetaAcidPercentage(), created.getBetaAcidPercentage());
        Assertions.assertEquals(change.getHarvestedAt(), created.getHarvestedAt());
        Assertions.assertEquals(change.getChangeGrams(), created.getChangeGrams());
        Assertions.assertEquals(user.getId(), change.getUser().getId());
        Assertions.assertEquals(hop.getId(), change.getHop().getId());

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1))
                .sumChangeByHopAndUser(
                        hop.getId(),
                        BigDecimal.valueOf(5.6),
                        BigDecimal.valueOf(2.2),
                        LocalDate.now(), user.getId()
                );
        Mockito.verify(hopChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createInsufficientStockFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-1000);

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // hop stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUser(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId()
        )).thenReturn(100);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> hopChangeService.create(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1))
                .sumChangeByHopAndUser(
                        hop.getId(),
                        BigDecimal.valueOf(5.6),
                        BigDecimal.valueOf(2.2),
                        LocalDate.now(), user.getId()
                );
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createHopNotFoundFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hopChangeService.create(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createUserNotFoundFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hopChangeService.create(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createUnauthorisedUserFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getAdmin();

        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));


        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hopChangeService.create(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUser() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Optional<HopChange> changeOptional = hopChangeService.readById(change.getId());
        Assertions.assertTrue(changeOptional.isPresent());
        Assertions.assertNotNull(changeOptional.get());
        Assertions.assertEquals(change.getId(), changeOptional.get().getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdAdmin() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Optional<HopChange> changeOptional = hopChangeService.readById(change.getId());
        Assertions.assertTrue(changeOptional.isPresent());
        Assertions.assertNotNull(changeOptional.get());
        Assertions.assertEquals(change.getId(), changeOptional.get().getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readByIdUnauthorised() {
        // prepare entities
        Hop hop = createHop();
        User user = getAdmin();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hopChangeService.readById(change.getId()));

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllUser() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findAllByUserId(getUser().getId())).thenReturn(List.of(change));

        List<HopChange> changes = hopChangeService.readAll();
        Assertions.assertEquals(1, changes.size());
        Assertions.assertNotNull(changes.get(0));
        Assertions.assertEquals(change.getId(), changes.get(0).getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findAllByUserId(getUser().getId());
        Mockito.verify(hopChangeRepository, Mockito.never()).findAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAdmin() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findAll()).thenReturn(List.of(change));

        List<HopChange> changes = hopChangeService.readAll();
        Assertions.assertEquals(1, changes.size());
        Assertions.assertNotNull(changes.get(0));
        Assertions.assertEquals(change.getId(), changes.get(0).getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findAll();
        Mockito.verify(hopChangeRepository, Mockito.never()).findAllByUserId(getUser().getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithUserAuth() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // hop stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(hopChangeRepository.save(change)).thenReturn(change);

        HopChange updated = hopChangeService.update(change);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(change.getId(), updated.getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1))
                .sumChangeByHopAndUserExceptChangeId(
                        hop.getId(),
                        BigDecimal.valueOf(5.6),
                        BigDecimal.valueOf(2.2),
                        LocalDate.now(), user.getId(),
                        change.getId()
                );
        Mockito.verify(hopChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateWithAdminAuth() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // hop stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        )).thenReturn(100);

        // mock save
        Mockito.when(hopChangeRepository.save(change)).thenReturn(change);

        HopChange updated = hopChangeService.update(change);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(change.getId(), updated.getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1))
                .sumChangeByHopAndUserExceptChangeId(
                        hop.getId(),
                        BigDecimal.valueOf(5.6),
                        BigDecimal.valueOf(2.2),
                        LocalDate.now(), user.getId(),
                        change.getId()
                );
        Mockito.verify(hopChangeRepository, Mockito.times(1)).save(change);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateInsufficientStockFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-1000);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // hop stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        )).thenReturn(100);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> hopChangeService.update(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1))
                .sumChangeByHopAndUserExceptChangeId(
                        hop.getId(),
                        BigDecimal.valueOf(5.6),
                        BigDecimal.valueOf(2.2),
                        LocalDate.now(), user.getId(),
                        change.getId()
                );
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateHopNotFoundFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-1000);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hopChangeService.update(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserNotFoundFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hopChangeService.update(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUnauthorisedUserFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getAdmin();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(-100);
        change.setCreatedAt(LocalDateTime.now());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hopChangeService.update(change));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        )).thenReturn(0);

        hopChangeService.deleteById(change.getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1)).sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        );
        Mockito.verify(hopChangeRepository, Mockito.times(1)).deleteById(change.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entities
        Hop hop = createHop();
        User user = getAdmin();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        )).thenReturn(0);

        hopChangeService.deleteById(change.getId());

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1)).sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        );
        Mockito.verify(hopChangeRepository, Mockito.times(1)).deleteById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUnauthorisedUserFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getAdmin();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hopChangeService.deleteById(change.getId()));

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdNotFoundFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getAdmin();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hopChangeService.deleteById(change.getId()));

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdInsufficientStockFail() {
        // prepare entities
        Hop hop = createHop();
        User user = getUser();

        HopChange change = new HopChange();
        change.setId(UUID.randomUUID());
        change.setHop(hop);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.6));
        change.setBetaAcidPercentage(BigDecimal.valueOf(2.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);

        // mock repository calls
        Mockito.when(hopChangeRepository.findById(change.getId())).thenReturn(Optional.of(change));
        // mock stock level
        Mockito.when(hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        )).thenReturn(-250);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> hopChangeService.deleteById(change.getId()));

        Mockito.verify(hopChangeRepository, Mockito.times(1)).findById(change.getId());
        Mockito.verify(hopChangeRepository, Mockito.times(1)).sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(5.6),
                BigDecimal.valueOf(2.2),
                LocalDate.now(), user.getId(),
                change.getId()
        );
    }

    /**
     * Create Hop entity instance with given name and manufacturer name
     *
     * @return Hop instance
     */
    private Hop createHop() {
        Hop hop = new Hop();
        hop.setId(UUID.randomUUID());
        hop.setName("Test hop name");
        hop.setAlphaAcidPercentage(BigDecimal.valueOf(7.7));
        hop.setBetaAcidPercentage(BigDecimal.valueOf(3.2));
        hop.setHopStorageIndex(BigDecimal.valueOf(0.000043));
        hop.setCreatedBy(createUser());

        return hop;
    }
}
