package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
class MaltRepositoryTest extends AbstractTest {

    @Autowired
    private MaltRepository maltRepository;

    @Autowired
    private MaltChangeRepository maltChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByNameAndManufacturerName() {
        // init
        User user = userRepository.save(createDummyUser());
        Malt withManufacturer = new Malt();
        withManufacturer.setName("Some malt name");
        withManufacturer.setManufacturerName("Manufacturer name");
        withManufacturer.setCreatedBy(user);

        Malt withoutManufacturer = new Malt();
        withoutManufacturer.setName("Some malt name");
        withoutManufacturer.setManufacturerName(null);
        withoutManufacturer.setCreatedBy(user);

        // persist
        maltRepository.save(withManufacturer);
        maltRepository.save(withoutManufacturer);

        // test
        Assertions.assertTrue(maltRepository.existsByNameAndManufacturerName("Some malt name", "Manufacturer name"));
        Assertions.assertTrue(maltRepository.existsByNameAndManufacturerName("Some malt name", null));
        Assertions.assertFalse(maltRepository.existsByNameAndManufacturerName("Some other name", null));
    }

    @Test
    void existsByNameAndManufacturerNameExceptId() {
        // init
        User user = userRepository.save(createDummyUser());
        Malt withManufacturer = new Malt();
        withManufacturer.setName("Some malt name");
        withManufacturer.setManufacturerName("Manufacturer name");
        withManufacturer.setCreatedBy(user);

        Malt withoutManufacturer = new Malt();
        withoutManufacturer.setName("Some malt name");
        withoutManufacturer.setManufacturerName(null);
        withoutManufacturer.setCreatedBy(user);

        // persist
        withManufacturer = maltRepository.save(withManufacturer);
        withoutManufacturer = maltRepository.save(withoutManufacturer);

        // test
        Assertions.assertTrue(maltRepository.existsByNameAndManufacturerNameExceptId("Some malt name", "Manufacturer name", UUID.randomUUID()));
        Assertions.assertTrue(maltRepository.existsByNameAndManufacturerNameExceptId("Some malt name", null, UUID.randomUUID()));
        Assertions.assertFalse(maltRepository.existsByNameAndManufacturerNameExceptId("Some other name", null, UUID.randomUUID()));

        Assertions.assertFalse(maltRepository.existsByNameAndManufacturerNameExceptId("Some malt name", "Manufacturer name", withManufacturer.getId()));
        Assertions.assertFalse(maltRepository.existsByNameAndManufacturerNameExceptId("Some malt name", null, withoutManufacturer.getId()));
    }

    @Test
    void existsByIdAndChangesIsNotNull() {
        // init
        User user = userRepository.save(createDummyUser());

        Malt withChange = new Malt();
        withChange.setName("Some malt name");
        withChange.setManufacturerName(null);
        withChange.setCreatedBy(user);
        MaltChange change = new MaltChange();
        change.setMalt(withChange);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());

        Malt withoutChange = new Malt();
        withoutChange.setName("Some malt name");
        withoutChange.setManufacturerName(null);
        withoutChange.setCreatedBy(user);

        // persist
        withChange = maltRepository.save(withChange);
        withoutChange = maltRepository.save(withoutChange);
        maltChangeRepository.save(change);

        // test
        Assertions.assertFalse(maltRepository.existsByIdAndChangesIsNotNull(UUID.randomUUID()));
        Assertions.assertFalse(maltRepository.existsByIdAndChangesIsNotNull(withoutChange.getId()));
        Assertions.assertTrue(maltRepository.existsByIdAndChangesIsNotNull(withChange.getId()));
    }


}
