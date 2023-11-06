package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@DataJpaTest
class BeerRepositoryTest extends AbstractTest {

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByCreatedById() {
        // init
        User user = userRepository.save(createUser());
        User userDummy = userRepository.save(createDummyUser());

        // prepare changes
        Beer beer = createBeer(user);
        Beer beerDummy = createBeer(userDummy);


        // persist
        beerRepository.save(beer);
        beerRepository.save(beerDummy);


        // test
        Assertions.assertEquals(0, beerRepository.findByCreatedById(UUID.randomUUID()).size());
        Assertions.assertEquals(1, beerRepository.findByCreatedById(user.getId()).size());
        Assertions.assertEquals(1, beerRepository.findByCreatedById(userDummy.getId()).size());

        // assert correct instances retrieved
        Assertions.assertEquals(beer.getId(), beerRepository.findByCreatedById(user.getId()).get(0).getId());
        Assertions.assertEquals(beerDummy.getId(), beerRepository.findByCreatedById(userDummy.getId()).get(0).getId());
    }

    /**
     * Helper method to create a dummy beer instance
     *
     * @param user user who should be marked as owner of the record
     * @return created beer
     */
    private Beer createBeer(User user) {
        Beer beer = new Beer();
        beer.setName("Citrus IPA");
        beer.setDescription("""
                    Citrus IPA: A radiant golden brew that marries the invigorating zest of freshly peeled oranges,
                    zesty grapefruits, and tangy tangerines with a harmonious burst of piney hoppy bitterness.
                    With its tantalizing aroma and refreshing, crisp mouthfeel, this beer is a perfect companion
                    for seafood, salads, grilled chicken, or spicier cuisine. The clean finish leaves a lasting
                    impression of citrus zest and a mild hoppy bitterness, inviting you to savor every sunny sip
                    of this vibrant, citrus-infused IPA.
                """);
        beer.setOriginalGravity(new BigDecimal("1.032").setScale(4, RoundingMode.HALF_UP));
        beer.setAlcoholByVolume(new BigDecimal("3.5").setScale(2, RoundingMode.HALF_UP));
        beer.setBitternessIbu(28);
        beer.setColorEbc(8);
        beer.setVolumeBrewed(new BigDecimal("25").setScale(1, RoundingMode.HALF_UP));
        beer.setVolumeRemaining(new BigDecimal("12.5").setScale(1, RoundingMode.HALF_UP));
        beer.setFinalGravityThreshold(new BigDecimal("1.005").setScale(4, RoundingMode.HALF_UP));
        beer.setFinalGravity(new BigDecimal("1.008").setScale(4, RoundingMode.HALF_UP));
        beer.setFermentationTemperatureThreshold(new BigDecimal("19.5").setScale(2, RoundingMode.HALF_UP));
        beer.setState(BrewStateEnum.DONE);
        beer.setBrewedAt(LocalDate.parse("2023-08-04"));
        beer.setFermentedAt(LocalDate.parse("2023-08-09"));
        beer.setMaturedAt(LocalDate.parse("2023-08-27"));
        beer.setConsumedAt(null);
        beer.setCreatedBy(user);

        return beer;
    }
}
