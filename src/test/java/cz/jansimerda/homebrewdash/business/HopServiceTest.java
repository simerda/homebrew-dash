package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Hop;
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
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class HopServiceTest extends AbstractServiceTest {

    @Autowired
    HopService hopService;

    @MockBean
    HopRepository hopRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void create() {
        // prepare entity
        Hop hop = createHop();

        // mock repository calls
        Mockito.when(hopRepository.existsByName(hop.getName())).thenReturn(false);
        // mock save
        Mockito.when(hopRepository.save(Mockito.any(Hop.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Hop hopToSave = (Hop) args[0];
                    hopToSave.setId(UUID.randomUUID());
                    return hopToSave;
                });

        Hop created = hopService.create(hop);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertEquals(hop.getName(), created.getName());
        Assertions.assertEquals(hop.getAlphaAcidPercentage(), created.getAlphaAcidPercentage());
        Assertions.assertEquals(hop.getBetaAcidPercentage(), created.getBetaAcidPercentage());
        Assertions.assertEquals(hop.getHopStorageIndex(), created.getHopStorageIndex());

        Mockito.verify(hopRepository, Mockito.times(1)).existsByName(hop.getName());
        Mockito.verify(hopRepository, Mockito.times(1)).save(hop);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createDuplicateFail() {
        // prepare entity
        Hop hop = createHop();

        // respond with not duplicate found
        Mockito.when(hopRepository.existsByName(hop.getName())).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> hopService.create(hop));

        Mockito.verify(hopRepository, Mockito.times(1)).existsByName(hop.getName());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.existsByNameExceptId(hop.getName(), hop.getId())).thenReturn(false);
        // mock save
        Mockito.when(hopRepository.save(hop)).thenReturn(hop);
        // mock find by ID
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));

        Hop updated = hopService.update(hop);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(hop.getId(), updated.getId());

        Mockito.verify(hopRepository, Mockito.times(1)).existsByNameExceptId(
                hop.getName(),
                hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).save(hop);
        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.existsByNameExceptId(hop.getName(), hop.getId())).thenReturn(false);
        // mock save
        Mockito.when(hopRepository.save(hop)).thenReturn(hop);
        // mock find by ID
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));

        Hop updated = hopService.update(hop);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(hop.getId(), updated.getId());

        Mockito.verify(hopRepository, Mockito.times(1)).existsByNameExceptId(
                hop.getName(),
                hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).save(hop);
        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserUnauthorisedFail() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(createDummyUser());

        // mock repository calls
        Mockito.when(hopRepository.existsByNameExceptId(hop.getName(), hop.getId())).thenReturn(false);
        // mock save
        Mockito.when(hopRepository.save(hop)).thenReturn(hop);
        // mock find by ID
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hopService.update(hop));

        Mockito.verify(hopRepository, Mockito.times(1)).existsByNameExceptId(
                hop.getName(),
                hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateDuplicateFail() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.existsByNameExceptId(
                hop.getName(),
                hop.getId())
        ).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> hopService.update(hop));

        Mockito.verify(hopRepository, Mockito.times(1)).existsByNameExceptId(
                hop.getName(),
                hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateNotFoundFail() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.existsByNameExceptId(hop.getName(), hop.getId())).thenReturn(false);
        // mock find by ID
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hopService.update(hop));

        Mockito.verify(hopRepository, Mockito.times(1)).existsByNameExceptId(
                hop.getName(),
                hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(hopRepository.existsByIdAndChangesIsNotNull(hop.getId())).thenReturn(false);

        hopService.deleteById(hop.getId());

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).deleteById(hop.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(hopRepository.existsByIdAndChangesIsNotNull(hop.getId())).thenReturn(false);

        hopService.deleteById(hop.getId());

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).deleteById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUserUnauthorisedFail() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(createDummyUser());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(hopRepository.existsByIdAndChangesIsNotNull(hop.getId())).thenReturn(false);

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> hopService.deleteById(hop.getId()));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdChangesAttachedFail() {
        // prepare entity
        Hop hop = createHop();
        hop.setId(UUID.randomUUID());
        hop.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(hopRepository.findById(hop.getId())).thenReturn(Optional.of(hop));
        Mockito.when(hopRepository.existsByIdAndChangesIsNotNull(hop.getId())).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> hopService.deleteById(hop.getId()));

        Mockito.verify(hopRepository, Mockito.times(1)).findById(hop.getId());
        Mockito.verify(hopRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(hop.getId());
    }

    /**
     * Create Hop entity instance with given name and manufacturer name
     *
     * @return Hop instance
     */
    private Hop createHop() {
        Hop hop = new Hop();
        hop.setName("Test hop");
        hop.setAlphaAcidPercentage(BigDecimal.valueOf(7.7));
        hop.setBetaAcidPercentage(BigDecimal.valueOf(3.2));
        hop.setHopStorageIndex(BigDecimal.valueOf(0.000043));

        return hop;
    }
}
