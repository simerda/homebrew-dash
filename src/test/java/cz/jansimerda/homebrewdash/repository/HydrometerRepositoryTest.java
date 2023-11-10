package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
class HydrometerRepositoryTest extends AbstractTest {

    @Autowired
    private HydrometerRepository hydrometerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getFirstByToken() {
        // init
        User user = userRepository.save(createUser());
        Hydrometer hydrometer = createHydrometer(user);

        // persist
        hydrometerRepository.save(hydrometer);

        // test
        Assertions.assertTrue(hydrometerRepository.getFirstByToken("some-other-token").isEmpty());
        Assertions.assertTrue(hydrometerRepository.getFirstByToken("test_token").isPresent());
    }

    @Test
    void findByCreatedById() {
        // init
        User user = userRepository.save(createUser());
        User dummyUser = userRepository.save(createDummyUser());

        Hydrometer hydrometerUser = createHydrometer(user);
        hydrometerUser.setToken("other-token");
        Hydrometer hydrometerDummyUser = createHydrometer(dummyUser);

        // persist
        hydrometerRepository.save(hydrometerUser);
        hydrometerRepository.save(hydrometerDummyUser);

        // test for presence
        Assertions.assertEquals(0, hydrometerRepository.findByCreatedById(UUID.randomUUID()).size());
        Assertions.assertEquals(1, hydrometerRepository.findByCreatedById(user.getId()).size());
        Assertions.assertEquals(1, hydrometerRepository.findByCreatedById(dummyUser.getId()).size());

        // test correct retrieved
        Assertions.assertEquals(hydrometerUser.getId(), hydrometerRepository.findByCreatedById(user.getId()).get(0).getId());
        Assertions.assertEquals(hydrometerDummyUser.getId(), hydrometerRepository.findByCreatedById(dummyUser.getId()).get(0).getId());
    }

    /**
     * Helper method to create a dummy hydrometer instance
     *
     * @param user user who should be marked as owner of the record
     * @return created hydrometer
     */
    private Hydrometer createHydrometer(User user) {
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setName("iSpindel");
        hydrometer.setToken("test_token");
        hydrometer.setAssignedBeer(null);
        hydrometer.setIsActive(true);
        hydrometer.setCreatedBy(user);
        hydrometer.setUpdatedAt(LocalDateTime.now());
        hydrometer.setCreatedAt(LocalDateTime.now());

        return hydrometer;
    }
}
