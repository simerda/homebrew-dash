package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
class MeasurementRepositoryTest extends AbstractTest {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByBeerCreatedById() {
        // init
        User user = userRepository.save(createUser());
        User dummyUser = userRepository.save(createDummyUser());

        Measurement measurementUser = createMeasurement(user);
        Measurement measurementDummyUser = createMeasurement(dummyUser);

        // persist
        measurementRepository.save(measurementUser);
        measurementRepository.save(measurementDummyUser);

        // test for presence
        Assertions.assertEquals(0, measurementRepository.findByBeerCreatedById(UUID.randomUUID()).size());
        Assertions.assertEquals(1, measurementRepository.findByBeerCreatedById(user.getId()).size());
        Assertions.assertEquals(1, measurementRepository.findByBeerCreatedById(dummyUser.getId()).size());

        // test correct retrieved
        Assertions.assertEquals(measurementUser.getId(), measurementRepository.findByBeerCreatedById(user.getId()).get(0).getId());
        Assertions.assertEquals(measurementDummyUser.getId(), measurementRepository.findByBeerCreatedById(dummyUser.getId()).get(0).getId());
    }

    /**
     * Helper method to create a dummy measurement instance
     *
     * @param user user who should be marked as owner of the beer related to the record
     * @return created measurement
     */
    private Measurement createMeasurement(User user) {
        Beer beer = new Beer();
        beer.setName("ABC Lager");
        beer.setState(BrewStateEnum.PLANNING);
        beer.setCreatedBy(user);
        beer.setUpdatedAt(LocalDateTime.now());
        beer.setCreatedAt(LocalDateTime.now());
        beerRepository.save(beer);

        Measurement measurement = new Measurement();
        measurement.setAngle(BigDecimal.valueOf(12.5));
        measurement.setTemperature(BigDecimal.valueOf(23.3));
        measurement.setBattery(BigDecimal.valueOf(2.5));
        measurement.setSpecificGravity(BigDecimal.valueOf(1.12));
        measurement.setInterval(300);
        measurement.setRssi(-73);
        measurement.setHydrometer(null);
        measurement.setBeer(beer);
        measurement.setIsHidden(false);
        measurement.setUpdatedAt(LocalDateTime.now());
        measurement.setCreatedAt(LocalDateTime.now());

        return measurement;
    }
}
