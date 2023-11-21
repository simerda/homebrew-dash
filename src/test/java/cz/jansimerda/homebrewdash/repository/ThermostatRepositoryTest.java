package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@DataJpaTest
class ThermostatRepositoryTest extends AbstractTest {

    @Autowired
    private ThermostatRepository thermostatRepository;

    @Autowired
    private HydrometerRepository hydrometerRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByCreatedById() {
        // init
        User user = userRepository.save(createUser());
        User dummyUser = userRepository.save(createDummyUser());

        Thermostat thermostatUser = createThermostat(user, null);
        Thermostat thermostatDummyUser = createThermostat(dummyUser, null);

        // persist
        thermostatRepository.save(thermostatUser);
        thermostatRepository.save(thermostatDummyUser);

        // test for presence
        Assertions.assertEquals(0, thermostatRepository.findByCreatedById(UUID.randomUUID()).size());
        Assertions.assertEquals(1, thermostatRepository.findByCreatedById(user.getId()).size());
        Assertions.assertEquals(1, thermostatRepository.findByCreatedById(dummyUser.getId()).size());

        // test correct retrieved
        Assertions.assertEquals(thermostatUser.getId(), thermostatRepository.findByCreatedById(user.getId()).get(0).getId());
        Assertions.assertEquals(thermostatDummyUser.getId(), thermostatRepository.findByCreatedById(dummyUser.getId()).get(0).getId());
    }

    @Test
    void findToBeSwitched() {
        User user = userRepository.save(createUser());

        Supplier<Beer> beerSupplier = () -> {
            Beer beer = new Beer();
            beer.setName("Beer name");
            beer.setFermentationTemperatureThreshold(BigDecimal.valueOf(20));
            beer.setState(BrewStateEnum.FERMENTING);
            beer.setBrewedAt(LocalDate.parse("2023-10-18"));
            beer.setCreatedBy(user);
            beer.setCreatedAt(LocalDateTime.now());
            beer.setUpdatedAt(LocalDateTime.now());
            return beer;
        };

        // init and persist
        Beer beer = beerSupplier.get();
        beerRepository.save(beer);

        Function<Beer, Hydrometer> hydrometerSupplier = (b) -> {
            Hydrometer hydrometer = new Hydrometer();
            hydrometer.setName("iSpindel");
            hydrometer.setToken(UUID.randomUUID().toString());
            hydrometer.setAssignedBeer(b);
            hydrometer.setCreatedBy(user);
            hydrometer.setUpdatedAt(LocalDateTime.now());
            hydrometer.setCreatedAt(LocalDateTime.now());
            return hydrometer;
        };
        Hydrometer hydrometer = hydrometerSupplier.apply(beer);
        hydrometerRepository.save(hydrometer);

        // thermostat to be switched
        Thermostat thermostat = createThermostat(user, hydrometer);
        thermostatRepository.save(thermostat);
        UUID thermostatOkId = thermostat.getId();

        // thermostat without hydrometer
        thermostatRepository.save(createThermostat(user, null));

        // failed recently
        beer = beerSupplier.get();
        beerRepository.save(beer);
        hydrometer = hydrometerSupplier.apply(beer);
        hydrometerRepository.save(hydrometer);
        thermostat = createThermostat(user, hydrometer);
        thermostat.setState(ThermostatStateEnum.SERVICE_ERROR);
        thermostat.setLastSuccessAt(LocalDateTime.now());
        thermostat.setLastFailAt(LocalDateTime.now().minusMinutes(40));
        thermostatRepository.save(thermostat);

        // succeeded recently
        beer = beerSupplier.get();
        beerRepository.save(beer);
        hydrometer = hydrometerSupplier.apply(beer);
        hydrometerRepository.save(hydrometer);
        thermostat = createThermostat(user, hydrometer);
        thermostat.setState(ThermostatStateEnum.ACTIVE);
        thermostat.setLastSuccessAt(LocalDateTime.now().minusMinutes(4));
        thermostat.setLastFailAt(LocalDateTime.now());
        thermostatRepository.save(thermostat);

        // beer not fermenting
        beer = beerSupplier.get();
        beer.setState(BrewStateEnum.PLANNING);
        beerRepository.save(beer);
        hydrometer = hydrometerSupplier.apply(beer);
        hydrometerRepository.save(hydrometer);
        thermostatRepository.save(createThermostat(user, hydrometer));

        // fermenting temp threshold not set
        beer = beerSupplier.get();
        beer.setFermentationTemperatureThreshold(null);
        beerRepository.save(beer);
        hydrometer = hydrometerSupplier.apply(beer);
        hydrometerRepository.save(hydrometer);
        thermostatRepository.save(createThermostat(user, hydrometer));

        // thermostat inactive
        beer = beerSupplier.get();
        beerRepository.save(beer);
        hydrometer = hydrometerSupplier.apply(beer);
        hydrometerRepository.save(hydrometer);
        thermostat = createThermostat(user, hydrometer);
        thermostat.setIsActive(false);
        thermostatRepository.save(thermostat);

        // test for presence
        Assertions.assertEquals(
                1,
                thermostatRepository.findToBeSwitched(
                        LocalDateTime.now().minusMinutes(5),
                        LocalDateTime.now().minusHours(1),
                        ThermostatStateEnum.SERVICE_ERROR,
                        BrewStateEnum.FERMENTING
                ).size()
        );

        // test correct retrieved
        Assertions.assertEquals(
                thermostatOkId,
                thermostatRepository.findToBeSwitched(
                        LocalDateTime.now().minusMinutes(5),
                        LocalDateTime.now().minusHours(1),
                        ThermostatStateEnum.SERVICE_ERROR,
                        BrewStateEnum.FERMENTING
                ).get(0).getId()
        );
    }

    /**
     * Helper method to create a dummy thermostat instance
     *
     * @param user       user who should be marked as owner of the record
     * @param hydrometer hydrometer that should be paired to the thermostat
     * @return created thermostat
     */
    private Thermostat createThermostat(User user, Hydrometer hydrometer) {
        Thermostat thermostat = new Thermostat();
        thermostat.setName("my iSpindel based thermostat");
        thermostat.setDeviceName("Smart Plug");
        thermostat.setEmail("some@mail.com");
        thermostat.setPassword("ThermostatServicePass123");
        thermostat.setIsHeating(true);
        thermostat.setIsActive(true);
        thermostat.setIsPoweredOn(false);
        thermostat.setState(ThermostatStateEnum.READY);
        thermostat.setHydrometer(hydrometer);
        thermostat.setLastSuccessAt(null);
        thermostat.setLastFailAt(null);
        thermostat.setCreatedBy(user);
        thermostat.setUpdatedAt(LocalDateTime.now());
        thermostat.setCreatedAt(LocalDateTime.now());

        return thermostat;
    }
}
