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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DataJpaTest
class YeastChangeRepositoryTest extends AbstractTest {

    @Autowired
    private YeastRepository yeastRepository;

    @Autowired
    private YeastChangeRepository yeastChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void sumChangeByYeastAndUser() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare yeast
        Yeast yeast = new Yeast();
        yeast.setName("Some yeast name");
        yeast.setManufacturerName("Manufacturer name");
        yeast.setKind(YeastKindEnum.DRIED);
        yeast.setType(YeastTypeEnum.ALE);
        yeast.setCreatedBy(user);

        // prepare changes
        List<YeastChange> changes = new ArrayList<>();
        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist yeast
        yeastRepository.save(yeast);

        // test zero sum
        Assertions.assertEquals(0, yeastChangeRepository.sumChangeByYeastAndUser(
                yeast.getId(),
                LocalDate.now(),
                user.getId()
        ));

        // store changes
        yeastChangeRepository.saveAll(changes);

        // test
        Assertions.assertEquals(70, yeastChangeRepository.sumChangeByYeastAndUser(
                yeast.getId(),
                LocalDate.now(),
                user.getId()
        ));
    }

    @Test
    void sumChangeByYeastAndUserExceptChangeId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare yeast
        Yeast yeast = new Yeast();
        yeast.setName("Some yeast name");
        yeast.setManufacturerName("Manufacturer name");
        yeast.setKind(YeastKindEnum.DRIED);
        yeast.setType(YeastTypeEnum.ALE);
        yeast.setCreatedBy(user);

        // prepare changes
        List<YeastChange> changes = new ArrayList<>();
        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist yeast
        yeastRepository.save(yeast);

        // test zero sum
        Assertions.assertEquals(0, yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                UUID.randomUUID()
        ));

        // store changes
        yeastChangeRepository.saveAll(changes);

        // test all
        Assertions.assertEquals(70, yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                UUID.randomUUID()
        ));
        Assertions.assertEquals(20, yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                yeast.getId(),
                LocalDate.now(),
                user.getId(),
                changes.get(2).getId()
        ));
    }

    @Test
    void findAllByUserId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare yeast
        Yeast yeast = new Yeast();
        yeast.setName("Some yeast name");
        yeast.setManufacturerName("Manufacturer name");
        yeast.setKind(YeastKindEnum.DRIED);
        yeast.setType(YeastTypeEnum.ALE);
        yeast.setCreatedBy(user);

        // prepare changes
        List<YeastChange> changes = new ArrayList<>();
        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist
        yeastRepository.save(yeast);
        yeastChangeRepository.saveAll(changes);


        // test
        Assertions.assertEquals(0, yeastChangeRepository.findAllByUserId(UUID.randomUUID()).size());
        Assertions.assertEquals(3, yeastChangeRepository.findAllByUserId(user.getId()).size());

        // ensure other user change not present
        UUID otherChangeId = change.getId();
        Assertions.assertTrue(yeastChangeRepository.findAllByUserId(user.getId()).stream()
                .noneMatch(c -> c.getId().equals(otherChangeId))
        );
    }
}
