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
import java.util.UUID;

@DataJpaTest
class HopRepositoryTest extends AbstractTest {

    @Autowired
    private HopRepository hopRepository;

    @Autowired
    private HopChangeRepository hopChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByName() {
        // init
        User user = userRepository.save(createDummyUser());
        Hop hop = new Hop();
        hop.setName("Some hop name");
        hop.setAlphaAcidPercentage(null);
        hop.setBetaAcidPercentage(null);
        hop.setHopStorageIndex(BigDecimal.valueOf(0.00007));
        hop.setCreatedBy(user);

        // persist
        hopRepository.save(hop);

        // test
        Assertions.assertTrue(hopRepository.existsByName("Some hop name"));
        Assertions.assertFalse(hopRepository.existsByName("Some other name"));
    }

    @Test
    void existsByNameExceptId() {
        // init
        User user = userRepository.save(createDummyUser());
        Hop hop = new Hop();
        hop.setName("Some hop name");
        hop.setAlphaAcidPercentage(null);
        hop.setBetaAcidPercentage(null);
        hop.setHopStorageIndex(BigDecimal.valueOf(0.00007));
        hop.setCreatedBy(user);

        // persist
        hopRepository.save(hop);

        // test
        Assertions.assertTrue(hopRepository.existsByNameExceptId("Some hop name", UUID.randomUUID()));
        Assertions.assertFalse(hopRepository.existsByNameExceptId("Some other name", UUID.randomUUID()));

        Assertions.assertFalse(hopRepository.existsByNameExceptId("Some hop name", hop.getId()));
        Assertions.assertFalse(hopRepository.existsByNameExceptId("Some other name", hop.getId()));
    }

    @Test
    void existsByIdAndChangesIsNotNull() {
        // init
        User user = userRepository.save(createDummyUser());

        Hop withChange = new Hop();
        withChange.setName("Galaxy");
        withChange.setAlphaAcidPercentage(null);
        withChange.setBetaAcidPercentage(null);
        withChange.setHopStorageIndex(BigDecimal.valueOf(0.00007));
        withChange.setCreatedBy(user);

        HopChange change = new HopChange();
        change.setHop(withChange);
        change.setUser(user);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(5.5));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.2));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);
        change.setCreatedAt(LocalDateTime.now());


        Hop withoutChange = new Hop();
        withoutChange.setName("Citra");
        withoutChange.setAlphaAcidPercentage(null);
        withoutChange.setBetaAcidPercentage(null);
        withoutChange.setHopStorageIndex(BigDecimal.valueOf(0.00007));
        withoutChange.setCreatedBy(user);

        // persist
        withChange = hopRepository.save(withChange);
        withoutChange = hopRepository.save(withoutChange);
        hopChangeRepository.save(change);

        // test
        Assertions.assertFalse(hopRepository.existsByIdAndChangesIsNotNull(UUID.randomUUID()));
        Assertions.assertFalse(hopRepository.existsByIdAndChangesIsNotNull(withoutChange.getId()));
        Assertions.assertTrue(hopRepository.existsByIdAndChangesIsNotNull(withChange.getId()));
    }
}
