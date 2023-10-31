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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DataJpaTest
class MaltChangeRepositoryTest extends AbstractTest {

    @Autowired
    private MaltRepository maltRepository;

    @Autowired
    private MaltChangeRepository maltChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void sumChangeByMaltIdAndUserId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare malt
        Malt malt = new Malt();
        malt.setName("Some malt name");
        malt.setManufacturerName(null);
        malt.setCreatedBy(user);

        // prepare changes
        List<MaltChange> changes = new ArrayList<>();
        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist malt
        maltRepository.save(malt);

        // test zero sum
        Assertions.assertEquals(0, maltChangeRepository.sumChangeByMaltIdAndUserId(malt.getId(), user.getId()));

        // store changes
        maltChangeRepository.saveAll(changes);

        // test
        Assertions.assertEquals(70, maltChangeRepository.sumChangeByMaltIdAndUserId(malt.getId(), user.getId()));
    }

    @Test
    void sumChangeByMaltIdAndUserIdExceptId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare malt
        Malt malt = new Malt();
        malt.setName("Some malt name");
        malt.setManufacturerName(null);
        malt.setCreatedBy(user);

        // prepare changes
        List<MaltChange> changes = new ArrayList<>();
        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist malt
        maltRepository.save(malt);

        // test zero sum
        Assertions.assertEquals(0, maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), UUID.randomUUID()));

        // store changes
        maltChangeRepository.saveAll(changes);

        // test all
        Assertions.assertEquals(70, maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), UUID.randomUUID()));
        Assertions.assertEquals(20, maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(malt.getId(), user.getId(), changes.get(2).getId()));
    }

    @Test
    void findAllByUserId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare malt
        Malt malt = new Malt();
        malt.setName("Some malt name");
        malt.setManufacturerName(null);
        malt.setCreatedBy(user);

        // prepare changes
        List<MaltChange> changes = new ArrayList<>();
        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new MaltChange();
        change.setMalt(malt);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist
        maltRepository.save(malt);
        maltChangeRepository.saveAll(changes);


        // test
        Assertions.assertEquals(0, maltChangeRepository.findAllByUserId(UUID.randomUUID()).size());
        Assertions.assertEquals(3, maltChangeRepository.findAllByUserId(user.getId()).size());

        // ensure other user change not present
        UUID otherChangeId = change.getId();
        Assertions.assertTrue(maltChangeRepository.findAllByUserId(user.getId()).stream()
                .noneMatch(c -> c.getId().equals(otherChangeId))
        );
    }
}
