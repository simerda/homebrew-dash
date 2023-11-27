package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
class BeerControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeerRepository beerRepository;

    @Override
    @AfterEach
    protected void tearDown() {
        beerRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void create() throws Exception {
        String description = """
                Juicy NEIPA is a hazy masterpiece, pouring a captivating, cloudy gold with a luscious, velvety head.
                Its tantalizing aroma is a burst of tropical fruit, citrus, and a hint of pine, setting the stage for
                a flavor explosion of ripe mango, passionfruit, and peach, balanced by a subtle malt sweetness and a
                refreshing citrus zing. This beer's silky-smooth body and gentle bitterness create a blissful cycle
                of flavor exploration, making it the perfect companion for a range of culinary delights, from juicy
                burgers to spicy curries and fresh seafood. Dive into the world of Juicy NEIPA, where each sip is a
                journey through flavor paradise.
                """.trim();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", description);
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Juicy NEIPA")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(description)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.specificGravity", Matchers.is(1.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.plato", Matchers.is("28.03")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alcoholByVolume", Matchers.is(5.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bitternessIbu", Matchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeBrewed", Matchers.is(23.4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeRemaining", Matchers.is(12.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.specificGravity", Matchers.is(1.05)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.plato", Matchers.is("12.39")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.specificGravity", Matchers.is(1.06)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.plato", Matchers.is("14.75")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentationTemperatureThreshold", Matchers.is(17.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(BrewStateEnum.DONE.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createBrewedAtNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.PLANNING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.nullValue()));
    }

    @Test
    void createBrewedAtSet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.BREWING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void createFermentedAtNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.FERMENTING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.nullValue()));
    }

    @Test
    void createFermentedAtSet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.MATURING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void createMaturedAtNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.MATURING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.nullValue()));
    }

    @Test
    void createMaturedAtSet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void createConsumedAtNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 20);
        data.put("volumeRemaining", 1);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.nullValue()));
    }

    @Test
    void createConsumedAtSet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 20);
        data.put("volumeRemaining", 0);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", null);
        data.put("description", "description".repeat(910));
        data.put("originalGravity", "2.12");
        data.put("alcoholByVolume", 100.38);
        data.put("bitternessIbu", -10);
        data.put("colorEbc", -2);
        data.put("volumeBrewed", -1);
        data.put("volumeRemaining", 2000000);
        data.put("finalGravityThreshold", -1.05);
        data.put("finalGravity", 0);
        data.put("fermentationTemperatureThreshold", "99.5");
        data.put("state", "COMPLETED");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'name')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'description')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'originalGravity')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'alcoholByVolume')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'bitternessIbu')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'colorEbc')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'volumeBrewed')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'volumeRemaining')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'finalGravityThreshold')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'finalGravity')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'fermentationTemperatureThreshold')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'state')]").exists());
    }

    @Test
    void createSetVolumeBrewedBeforeFermentingFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.BREWING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void createSetVolumeRemainingBeforeVolumeBrewedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", 23.4);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.FERMENTING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void createSetVolumeRemainingWhenBotchedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 26);
        data.put("volumeRemaining", 23.4);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.BOTCHED.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void createSetVolumeRemainingOverVolumeBrewedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 20);
        data.put("volumeRemaining", 23.4);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void createUnauthenticatedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void readAllUser() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(beer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].description", Matchers.hasItem(beer.getDescription().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].originalGravity.specificGravity", Matchers.hasItem(beer.getOriginalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].originalGravity.plato", Matchers.hasItem("8.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].alcoholByVolume", Matchers.hasItem(beer.getAlcoholByVolume().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].bitternessIbu", Matchers.hasItem(beer.getBitternessIbu().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].colorEbc", Matchers.hasItem(beer.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].volumeBrewed", Matchers.hasItem(beer.getVolumeBrewed().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].volumeRemaining", Matchers.hasItem(beer.getVolumeRemaining().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravityThreshold.specificGravity", Matchers.hasItem(beer.getFinalGravityThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravityThreshold.plato", Matchers.hasItem("1.29")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravity.specificGravity", Matchers.hasItem(beer.getFinalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravity.plato", Matchers.hasItem("2.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].fermentationTemperatureThreshold", Matchers.hasItem(beer.getFermentationTemperatureThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].state", Matchers.hasItem(beer.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].brewedAt", Matchers.hasItem(beer.getBrewedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].fermentedAt", Matchers.hasItem(beer.getFermentedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].maturedAt", Matchers.hasItem(beer.getMaturedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].consumedAt", Matchers.hasItem(beer.getConsumedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdByUserId", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readAllAdmin() throws Exception {
        Beer beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateAdmin())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(beer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].description", Matchers.hasItem(beer.getDescription().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].originalGravity.specificGravity", Matchers.hasItem(beer.getOriginalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].originalGravity.plato", Matchers.hasItem("8.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].alcoholByVolume", Matchers.hasItem(beer.getAlcoholByVolume().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].bitternessIbu", Matchers.hasItem(beer.getBitternessIbu().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].colorEbc", Matchers.hasItem(beer.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].volumeBrewed", Matchers.hasItem(beer.getVolumeBrewed().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].volumeRemaining", Matchers.hasItem(beer.getVolumeRemaining().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravityThreshold.specificGravity", Matchers.hasItem(beer.getFinalGravityThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravityThreshold.plato", Matchers.hasItem("1.29")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravity.specificGravity", Matchers.hasItem(beer.getFinalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].finalGravity.plato", Matchers.hasItem("2.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].fermentationTemperatureThreshold", Matchers.hasItem(beer.getFermentationTemperatureThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].state", Matchers.hasItem(beer.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].brewedAt", Matchers.hasItem(beer.getBrewedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].fermentedAt", Matchers.hasItem(beer.getFermentedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].maturedAt", Matchers.hasItem(beer.getMaturedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].consumedAt", Matchers.hasItem(beer.getConsumedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdByUserId", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readAllUnauthenticatedFail() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void readOneUser() throws Exception {
        Beer beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(beer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(beer.getDescription().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.specificGravity", Matchers.is(beer.getOriginalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.plato", Matchers.is("8.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alcoholByVolume", Matchers.is(beer.getAlcoholByVolume().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bitternessIbu", Matchers.is(beer.getBitternessIbu().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(beer.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeBrewed", Matchers.is(beer.getVolumeBrewed().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeRemaining", Matchers.is(beer.getVolumeRemaining().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.specificGravity", Matchers.is(beer.getFinalGravityThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.plato", Matchers.is("1.29")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.specificGravity", Matchers.is(beer.getFinalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.plato", Matchers.is("2.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentationTemperatureThreshold", Matchers.is(beer.getFermentationTemperatureThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(beer.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(beer.getBrewedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.is(beer.getFermentedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(beer.getMaturedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.is(beer.getConsumedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readOneAdmin() throws Exception {
        Beer beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(beer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(beer.getDescription().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.specificGravity", Matchers.is(beer.getOriginalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.plato", Matchers.is("8.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alcoholByVolume", Matchers.is(beer.getAlcoholByVolume().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bitternessIbu", Matchers.is(beer.getBitternessIbu().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(beer.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeBrewed", Matchers.is(beer.getVolumeBrewed().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeRemaining", Matchers.is(beer.getVolumeRemaining().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.specificGravity", Matchers.is(beer.getFinalGravityThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.plato", Matchers.is("1.29")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.specificGravity", Matchers.is(beer.getFinalGravity().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.plato", Matchers.is("2.05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentationTemperatureThreshold", Matchers.is(beer.getFermentationTemperatureThreshold().map(BigDecimal::doubleValue).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(beer.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(beer.getBrewedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.is(beer.getFermentedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(beer.getMaturedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.is(beer.getConsumedAt().map(LocalDate::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readOneUnauthorisedFail() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        Beer beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(beer.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void readOneNotFoundFail() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(UUID.randomUUID()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateUser() throws Exception {
        Beer beer = createBeer();

        String description = """
                Juicy NEIPA is a hazy masterpiece, pouring a captivating, cloudy gold with a luscious, velvety head.
                Its tantalizing aroma is a burst of tropical fruit, citrus, and a hint of pine, setting the stage for
                a flavor explosion of ripe mango, passionfruit, and peach, balanced by a subtle malt sweetness and a
                refreshing citrus zing. This beer's silky-smooth body and gentle bitterness create a blissful cycle
                of flavor exploration, making it the perfect companion for a range of culinary delights, from juicy
                burgers to spicy curries and fresh seafood. Dive into the world of Juicy NEIPA, where each sip is a
                journey through flavor paradise.
                """.trim();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", description);
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Juicy NEIPA")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(description)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.specificGravity", Matchers.is(1.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.plato", Matchers.is("28.03")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alcoholByVolume", Matchers.is(5.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bitternessIbu", Matchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeBrewed", Matchers.is(23.4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeRemaining", Matchers.is(12.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.specificGravity", Matchers.is(1.05)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.plato", Matchers.is("12.39")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.specificGravity", Matchers.is(1.06)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.plato", Matchers.is("14.75")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentationTemperatureThreshold", Matchers.is(17.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(BrewStateEnum.DONE.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateAdmin() throws Exception {
        Beer beer = createBeer();

        String description = """
                Juicy NEIPA is a hazy masterpiece, pouring a captivating, cloudy gold with a luscious, velvety head.
                Its tantalizing aroma is a burst of tropical fruit, citrus, and a hint of pine, setting the stage for
                a flavor explosion of ripe mango, passionfruit, and peach, balanced by a subtle malt sweetness and a
                refreshing citrus zing. This beer's silky-smooth body and gentle bitterness create a blissful cycle
                of flavor exploration, making it the perfect companion for a range of culinary delights, from juicy
                burgers to spicy curries and fresh seafood. Dive into the world of Juicy NEIPA, where each sip is a
                journey through flavor paradise.
                """.trim();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", description);
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Juicy NEIPA")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(description)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.specificGravity", Matchers.is(1.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.originalGravity.plato", Matchers.is("28.03")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alcoholByVolume", Matchers.is(5.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bitternessIbu", Matchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeBrewed", Matchers.is(23.4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.volumeRemaining", Matchers.is(12.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.specificGravity", Matchers.is(1.05)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravityThreshold.plato", Matchers.is("12.39")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.specificGravity", Matchers.is(1.06)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalGravity.plato", Matchers.is("14.75")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentationTemperatureThreshold", Matchers.is(17.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(BrewStateEnum.DONE.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fermentedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateBrewedAtNull() throws Exception {
        Beer beer = createBeer();
        beer.setBrewedAt(LocalDate.now());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.PLANNING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.nullValue()));
    }

    @Test
    void updateBrewedAtSet() throws Exception {
        Beer beer = createBeer();
        beer.setBrewedAt(null);
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.BREWING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void updateFermentedAtNull() throws Exception {
        Beer beer = createBeer();
        beer.setFermentedAt(LocalDate.now());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.FERMENTING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void updateFermentedAtSet() throws Exception {
        Beer beer = createBeer();
        beer.setFermentedAt(null);
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.MATURING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brewedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void updateMaturedAtNull() throws Exception {
        Beer beer = createBeer();
        beer.setMaturedAt(LocalDate.now());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.MATURING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.nullValue()));
    }

    @Test
    void updateMaturedAtSet() throws Exception {
        Beer beer = createBeer();
        beer.setMaturedAt(null);
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maturedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void updateConsumedAtNull() throws Exception {
        Beer beer = createBeer();
        beer.setConsumedAt(LocalDate.now());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 15);
        data.put("volumeRemaining", .5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.nullValue()));
    }

    @Test
    void updateConsumedAtSet() throws Exception {
        Beer beer = createBeer();
        beer.setConsumedAt(null);
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 15);
        data.put("volumeRemaining", 0);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumedAt", Matchers.is(LocalDate.now().toString())));
    }

    @Test
    void updateValidationFail() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", null);
        data.put("description", "description".repeat(910));
        data.put("originalGravity", "2.12");
        data.put("alcoholByVolume", 100.38);
        data.put("bitternessIbu", -10);
        data.put("colorEbc", -2);
        data.put("volumeBrewed", -1);
        data.put("volumeRemaining", 2000000);
        data.put("finalGravityThreshold", -1.05);
        data.put("finalGravity", 0);
        data.put("fermentationTemperatureThreshold", "99.5");
        data.put("state", "COMPLETED");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'name')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'description')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'originalGravity')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'alcoholByVolume')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'bitternessIbu')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'colorEbc')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'volumeBrewed')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'volumeRemaining')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'finalGravityThreshold')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'finalGravity')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'fermentationTemperatureThreshold')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'state')]").exists());
    }

    @Test
    void updateSetVolumeBrewedBeforeFermentingFail() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", null);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.BREWING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void updateSetVolumeRemainingBeforeVolumeBrewedFail() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", null);
        data.put("volumeRemaining", 23.4);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.FERMENTING.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void updateSetVolumeRemainingWhenBotchedFail() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 26);
        data.put("volumeRemaining", 23.4);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.BOTCHED.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void updateSetVolumeRemainingOverVolumeBrewedFail() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 20);
        data.put("volumeRemaining", 23.4);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("CONDITIONS_NOT_MET")));
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUnauthorisedFail() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Juicy NEIPA");
        data.put("description", "Juice NEIPA is a fresh summer beer");
        data.put("originalGravity", "1.12");
        data.put("alcoholByVolume", 5.38);
        data.put("bitternessIbu", 12);
        data.put("colorEbc", 7);
        data.put("volumeBrewed", 23.4);
        data.put("volumeRemaining", 12.5);
        data.put("finalGravityThreshold", 1.05);
        data.put("finalGravity", 1.06);
        data.put("fermentationTemperatureThreshold", "17.5");
        data.put("state", BrewStateEnum.DONE.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(UUID.randomUUID()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteUser() throws Exception {
        Beer beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Beer beer = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Beer change = createBeer();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(change.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorisedFail() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(beer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteNotFoundFail() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(UUID.randomUUID()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Create a dummy Beer entity and persist it into the DB
     *
     * @return Beer entity
     */
    private Beer createBeer() {
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
        beer.setOriginalGravity(new BigDecimal("1.032"));
        beer.setAlcoholByVolume(new BigDecimal("3.5"));
        beer.setBitternessIbu(28);
        beer.setColorEbc(8);

        beer.setVolumeBrewed(new BigDecimal("25"));
        beer.setVolumeRemaining(new BigDecimal("12.5"));
        beer.setFinalGravityThreshold(new BigDecimal("1.005"));
        beer.setFinalGravity(new BigDecimal("1.008"));
        beer.setFermentationTemperatureThreshold(new BigDecimal("19.5"));
        beer.setState(BrewStateEnum.DONE);
        beer.setCreatedBy(getUser());

        return beerRepository.save(beer);
    }

    private String getUri() {
        return "/api/v1/beers";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
