package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DataJpaTest
class HopChangeRepositoryTest extends AbstractTest {

    @Autowired
    private HopRepository hopRepository;

    @Autowired
    private HopChangeRepository hopChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void sumChangeByHopAndUser() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare hop
        Hop hop = new Hop();
        hop.setName("Some hop name");
        hop.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        hop.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        hop.setHopStorageIndex(BigDecimal.valueOf(0.001));
        hop.setCreatedBy(user);

        // prepare changes
        List<HopChange> changes = new ArrayList<>();
        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist hop
        hopRepository.save(hop);

        // test zero sum
        Assertions.assertEquals(0, hopChangeRepository.sumChangeByHopAndUser(
                hop.getId(),
                BigDecimal.valueOf(6.4),
                BigDecimal.valueOf(3.3),
                LocalDate.now(),
                user.getId()
        ));

        // store changes
        hopChangeRepository.saveAll(changes);

        // test
        Assertions.assertEquals(70, hopChangeRepository.sumChangeByHopAndUser(
                hop.getId(),
                BigDecimal.valueOf(6.4),
                BigDecimal.valueOf(3.3),
                LocalDate.now(),
                user.getId()
        ));
    }

    @Test
    void sumChangeByHopAndUserExceptChangeId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare hop
        Hop hop = new Hop();
        hop.setName("Some hop name");
        hop.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        hop.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        hop.setHopStorageIndex(BigDecimal.valueOf(0.001));
        hop.setCreatedBy(user);

        // prepare changes
        List<HopChange> changes = new ArrayList<>();
        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist hop
        hopRepository.save(hop);

        // test zero sum
        Assertions.assertEquals(0, hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(6.4),
                BigDecimal.valueOf(3.3),
                LocalDate.now(),
                user.getId(),
                UUID.randomUUID()
        ));

        // store changes
        hopChangeRepository.saveAll(changes);

        // test all
        Assertions.assertEquals(70, hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(6.4),
                BigDecimal.valueOf(3.3),
                LocalDate.now(),
                user.getId(),
                UUID.randomUUID()
        ));
        Assertions.assertEquals(20, hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                hop.getId(),
                BigDecimal.valueOf(6.4),
                BigDecimal.valueOf(3.3),
                LocalDate.now(),
                user.getId(),
                changes.get(2).getId()
        ));
    }

    @Test
    void findAllByUserId() {
        // init
        User user = userRepository.save(createDummyUser());

        // prepare hop
        Hop hop = new Hop();
        hop.setName("Some hop name");
        hop.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        hop.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        hop.setHopStorageIndex(BigDecimal.valueOf(0.001));
        hop.setCreatedBy(user);

        // prepare changes
        List<HopChange> changes = new ArrayList<>();
        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(100);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(-80);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(user);
        change.setChangeGrams(50);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);

        change = new HopChange();
        change.setHop(hop);
        change.setUser(userRepository.save(createUser()));
        change.setChangeGrams(1000);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(6.4));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.3));
        change.setHarvestedAt(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        changes.add(change);


        // persist
        hopRepository.save(hop);
        hopChangeRepository.saveAll(changes);


        // test
        Assertions.assertEquals(0, hopChangeRepository.findAllByUserId(UUID.randomUUID()).size());
        Assertions.assertEquals(3, hopChangeRepository.findAllByUserId(user.getId()).size());

        // ensure other user change not present
        UUID otherChangeId = change.getId();
        Assertions.assertTrue(hopChangeRepository.findAllByUserId(user.getId()).stream()
                .noneMatch(c -> c.getId().equals(otherChangeId))
        );
    }
}
