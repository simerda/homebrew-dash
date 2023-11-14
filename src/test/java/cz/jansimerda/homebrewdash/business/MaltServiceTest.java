package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Malt;
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

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class MaltServiceTest extends AbstractServiceTest {

    @Autowired
    MaltService maltService;

    @MockBean
    MaltRepository maltRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void create() {
        // prepare entity
        Malt malt = createMalt();

        // mock repository calls
        Mockito.when(maltRepository.existsByNameAndManufacturerName(
                malt.getName(),
                malt.getManufacturerName().orElse(null))
        ).thenReturn(false);
        // mock save
        Mockito.when(maltRepository.save(Mockito.any(Malt.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Malt maltToSave = (Malt) args[0];
                    maltToSave.setId(UUID.randomUUID());
                    return maltToSave;
                });

        Malt created = maltService.create(malt);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertEquals(malt.getName(), created.getName());
        Assertions.assertEquals(malt.getManufacturerName(), created.getManufacturerName());

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerName(
                malt.getName(),
                malt.getManufacturerName().orElse(null));
        Mockito.verify(maltRepository, Mockito.times(1)).save(malt);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createDuplicateFail() {
        // prepare entity
        Malt malt = createMalt();
        Malt mockedMalt = createMalt();
        mockedMalt.setId(UUID.randomUUID());

        // respond with not duplicate found
        Mockito.when(maltRepository.existsByNameAndManufacturerName(
                malt.getName(),
                malt.getManufacturerName().orElse(null))
        ).thenReturn(true);


        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> maltService.create(malt));

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerName(
                malt.getName(),
                malt.getManufacturerName().orElse(null));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId())
        ).thenReturn(false);
        // mock save
        Mockito.when(maltRepository.save(malt)).thenReturn(malt);
        // mock find by ID
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));

        Malt updated = maltService.update(malt);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(malt.getId(), updated.getId());

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).save(malt);
        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId())
        ).thenReturn(false);
        // mock save
        Mockito.when(maltRepository.save(malt)).thenReturn(malt);
        // mock find by ID
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));

        Malt updated = maltService.update(malt);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(malt.getId(), updated.getId());

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).save(malt);
        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserUnauthorisedFail() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(createDummyUser());

        // mock repository calls
        Mockito.when(maltRepository.existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId())
        ).thenReturn(false);
        // mock save
        Mockito.when(maltRepository.save(malt)).thenReturn(malt);
        // mock find by ID
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> maltService.update(malt));

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateDuplicateFail() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId())
        ).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> maltService.update(malt));

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateNotFoundFail() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId())
        ).thenReturn(false);
        // mock find by ID
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> maltService.update(malt));

        Mockito.verify(maltRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                malt.getName(),
                malt.getManufacturerName().orElse(null),
                malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(maltRepository.existsByIdAndChangesIsNotNull(malt.getId())).thenReturn(false);

        maltService.deleteById(malt.getId());

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).deleteById(malt.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(maltRepository.existsByIdAndChangesIsNotNull(malt.getId())).thenReturn(false);

        maltService.deleteById(malt.getId());

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).deleteById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUserUnauthorisedFail() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(createDummyUser());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(maltRepository.existsByIdAndChangesIsNotNull(malt.getId())).thenReturn(false);

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> maltService.deleteById(malt.getId()));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdChangesAttachedFail() {
        // prepare entity
        Malt malt = createMalt();
        malt.setId(UUID.randomUUID());
        malt.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(maltRepository.findById(malt.getId())).thenReturn(Optional.of(malt));
        Mockito.when(maltRepository.existsByIdAndChangesIsNotNull(malt.getId())).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> maltService.deleteById(malt.getId()));

        Mockito.verify(maltRepository, Mockito.times(1)).findById(malt.getId());
        Mockito.verify(maltRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(malt.getId());
    }

    /**
     * Create Malt entity instance with given name and manufacturer name
     *
     * @return Malt instance
     */
    private Malt createMalt() {
        Malt malt = new Malt();
        malt.setName("Test malt");
        malt.setManufacturerName("Test manufacturer");

        return malt;
    }
}
