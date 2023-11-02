package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;
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

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class YeastServiceTest extends AbstractServiceTest {

    @Autowired
    YeastService yeastService;

    @MockBean
    YeastRepository yeastRepository;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void create() {
        // prepare entity
        Yeast yeast = createYeast();

        // mock repository calls
        Mockito.when(yeastRepository.existsByNameAndManufacturerName(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null))
        ).thenReturn(false);
        // mock save
        Mockito.when(yeastRepository.save(Mockito.any(Yeast.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Yeast yeastToSave = (Yeast) args[0];
                    yeastToSave.setId(UUID.randomUUID());
                    return yeastToSave;
                });

        Yeast created = yeastService.create(yeast);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(getUser().getId(), created.getCreatedBy().getId());
        Assertions.assertEquals(yeast.getName(), created.getName());
        Assertions.assertEquals(yeast.getManufacturerName(), created.getManufacturerName());
        Assertions.assertEquals(yeast.getKind(), created.getKind());
        Assertions.assertEquals(yeast.getType(), created.getType());

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerName(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null));
        Mockito.verify(yeastRepository, Mockito.times(1)).save(yeast);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createDuplicateFail() {
        // prepare entity
        Yeast yeast = createYeast();
        Yeast mockedYeast = createYeast();
        mockedYeast.setId(UUID.randomUUID());

        // respond with not duplicate found
        Mockito.when(yeastRepository.existsByNameAndManufacturerName(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null))
        ).thenReturn(true);


        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> yeastService.create(yeast));

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerName(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUser() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId())
        ).thenReturn(false);
        // mock save
        Mockito.when(yeastRepository.save(yeast)).thenReturn(yeast);
        // mock find by ID
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));

        Yeast updated = yeastService.update(yeast);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(yeast.getId(), updated.getId());

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).save(yeast);
        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateAdmin() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId())
        ).thenReturn(false);
        // mock save
        Mockito.when(yeastRepository.save(yeast)).thenReturn(yeast);
        // mock find by ID
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));

        Yeast updated = yeastService.update(yeast);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(yeast.getId(), updated.getId());

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).save(yeast);
        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateUserUnauthorisedFail() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(createDummyUser());

        // mock repository calls
        Mockito.when(yeastRepository.existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId())
        ).thenReturn(false);
        // mock save
        Mockito.when(yeastRepository.save(yeast)).thenReturn(yeast);
        // mock find by ID
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> yeastService.update(yeast));

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateDuplicateFail() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId())
        ).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> yeastService.update(yeast));

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateNotFoundFail() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId())
        ).thenReturn(false);
        // mock find by ID
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> yeastService.update(yeast));

        Mockito.verify(yeastRepository, Mockito.times(1)).existsByNameAndManufacturerNameExceptId(
                yeast.getName(),
                yeast.getManufacturerName().orElse(null),
                yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUser() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(yeastRepository.existsByIdAndChangesIsNotNull(yeast.getId())).thenReturn(false);

        yeastService.deleteById(yeast.getId());

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).deleteById(yeast.getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdAdmin() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(yeastRepository.existsByIdAndChangesIsNotNull(yeast.getId())).thenReturn(false);

        yeastService.deleteById(yeast.getId());

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).deleteById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdUserUnauthorisedFail() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(createDummyUser());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(yeastRepository.existsByIdAndChangesIsNotNull(yeast.getId())).thenReturn(false);

        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> yeastService.deleteById(yeast.getId()));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteByIdChangesAttachedFail() {
        // prepare entity
        Yeast yeast = createYeast();
        yeast.setId(UUID.randomUUID());
        yeast.setCreatedBy(getUser());

        // mock repository calls
        Mockito.when(yeastRepository.findById(yeast.getId())).thenReturn(Optional.of(yeast));
        Mockito.when(yeastRepository.existsByIdAndChangesIsNotNull(yeast.getId())).thenReturn(true);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> yeastService.deleteById(yeast.getId()));

        Mockito.verify(yeastRepository, Mockito.times(1)).findById(yeast.getId());
        Mockito.verify(yeastRepository, Mockito.times(1)).existsByIdAndChangesIsNotNull(yeast.getId());
    }

    /**
     * Create Yeast entity instance with given name and manufacturer name
     *
     * @return Yeast instance
     */
    private Yeast createYeast() {
        Yeast yeast = new Yeast();
        yeast.setName("Test yeast");
        yeast.setManufacturerName("Test manufacturer");
        yeast.setKind(YeastKindEnum.LIQUID);
        yeast.setType(YeastTypeEnum.LAGER);

        return yeast;
    }
}
