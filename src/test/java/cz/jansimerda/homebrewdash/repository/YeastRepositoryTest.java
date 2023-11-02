package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
class YeastRepositoryTest extends AbstractTest {

    @Autowired
    private YeastRepository yeastRepository;

    @Autowired
    private YeastChangeRepository yeastChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByNameAndManufacturerName() {
        // init
        User user = userRepository.save(createDummyUser());
        Yeast withManufacturer = new Yeast();
        withManufacturer.setName("Some yeast name");
        withManufacturer.setManufacturerName("Manufacturer name");
        withManufacturer.setKind(YeastKindEnum.DRIED);
        withManufacturer.setType(YeastTypeEnum.ALE);
        withManufacturer.setCreatedBy(user);

        Yeast withoutManufacturer = new Yeast();
        withoutManufacturer.setName("Some yeast name");
        withoutManufacturer.setManufacturerName(null);
        withoutManufacturer.setKind(YeastKindEnum.DRIED);
        withoutManufacturer.setType(YeastTypeEnum.ALE);
        withoutManufacturer.setCreatedBy(user);

        // persist
        yeastRepository.save(withManufacturer);
        yeastRepository.save(withoutManufacturer);

        // test
        Assertions.assertTrue(yeastRepository.existsByNameAndManufacturerName("Some yeast name", "Manufacturer name"));
        Assertions.assertTrue(yeastRepository.existsByNameAndManufacturerName("Some yeast name", null));
        Assertions.assertFalse(yeastRepository.existsByNameAndManufacturerName("Some other name", null));
    }

    @Test
    void existsByNameAndManufacturerNameExceptId() {
        // init
        User user = userRepository.save(createDummyUser());
        Yeast withManufacturer = new Yeast();
        withManufacturer.setName("Some yeast name");
        withManufacturer.setManufacturerName("Manufacturer name");
        withManufacturer.setKind(YeastKindEnum.DRIED);
        withManufacturer.setType(YeastTypeEnum.ALE);
        withManufacturer.setCreatedBy(user);

        Yeast withoutManufacturer = new Yeast();
        withoutManufacturer.setName("Some yeast name");
        withoutManufacturer.setManufacturerName(null);
        withoutManufacturer.setKind(YeastKindEnum.DRIED);
        withoutManufacturer.setType(YeastTypeEnum.ALE);
        withoutManufacturer.setCreatedBy(user);

        // persist
        withManufacturer = yeastRepository.save(withManufacturer);
        withoutManufacturer = yeastRepository.save(withoutManufacturer);

        // test
        Assertions.assertTrue(yeastRepository.existsByNameAndManufacturerNameExceptId("Some yeast name", "Manufacturer name", UUID.randomUUID()));
        Assertions.assertTrue(yeastRepository.existsByNameAndManufacturerNameExceptId("Some yeast name", null, UUID.randomUUID()));
        Assertions.assertFalse(yeastRepository.existsByNameAndManufacturerNameExceptId("Some other name", null, UUID.randomUUID()));

        Assertions.assertFalse(yeastRepository.existsByNameAndManufacturerNameExceptId("Some yeast name", "Manufacturer name", withManufacturer.getId()));
        Assertions.assertFalse(yeastRepository.existsByNameAndManufacturerNameExceptId("Some yeast name", null, withoutManufacturer.getId()));
    }

    @Test
    void existsByIdAndChangesIsNotNull() {
        // init
        User user = userRepository.save(createDummyUser());

        Yeast withChange = new Yeast();
        withChange.setName("Some yeast name");
        withChange.setManufacturerName(null);
        withChange.setKind(YeastKindEnum.DRIED);
        withChange.setType(YeastTypeEnum.ALE);
        withChange.setCreatedBy(user);
        YeastChange change = new YeastChange();
        change.setYeast(withChange);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setExpirationDate(LocalDate.now().plusMonths(8));
        change.setCreatedAt(LocalDateTime.now());

        Yeast withoutChange = new Yeast();
        withoutChange.setName("Some yeast name");
        withoutChange.setManufacturerName(null);
        withoutChange.setKind(YeastKindEnum.DRIED);
        withoutChange.setType(YeastTypeEnum.ALE);
        withoutChange.setCreatedBy(user);

        // persist
        withChange = yeastRepository.save(withChange);
        withoutChange = yeastRepository.save(withoutChange);
        yeastChangeRepository.save(change);

        // test
        Assertions.assertFalse(yeastRepository.existsByIdAndChangesIsNotNull(UUID.randomUUID()));
        Assertions.assertFalse(yeastRepository.existsByIdAndChangesIsNotNull(withoutChange.getId()));
        Assertions.assertTrue(yeastRepository.existsByIdAndChangesIsNotNull(withChange.getId()));
    }
}
