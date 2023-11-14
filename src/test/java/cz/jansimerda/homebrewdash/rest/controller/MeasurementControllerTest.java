package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.exception.exposed.ExposedExceptionTypeEnum;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import cz.jansimerda.homebrewdash.repository.MeasurementRepository;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
public class MeasurementControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private HydrometerRepository hydrometerRepository;

    @Autowired
    private BeerRepository beerRepository;

    @AfterEach
    @Override
    protected void tearDown() {
        measurementRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void create() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", hydrometer.getToken());
        data.put("angle", "123.12345678");
        data.put("temperature", 101.234);
        data.put("temp_units", "C");
        data.put("battery", "3.123456789");
        data.put("gravity", "123.12345678");
        data.put("interval", 100);
        data.put("RSSI", -65);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.angle", Matchers.is(123.12345678)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature", Matchers.is(101.234)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.battery", Matchers.is(3.123456789)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gravity.specificGravity", Matchers.is(123.12345678)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.interval", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rssi", Matchers.is(-65)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometerId", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createNotHidden() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", hydrometer.getToken());
        data.put("angle", "123.12345678");
        data.put("temperature", 101.234);
        data.put("temp_units", "C");
        data.put("battery", "3.123456789");
        data.put("gravity", "1.123456");
        data.put("interval", 100);
        data.put("RSSI", -65);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.angle", Matchers.is(123.12345678)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature", Matchers.is(101.234)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.battery", Matchers.is(3.123456789)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gravity.specificGravity", Matchers.is(1.123456)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.interval", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rssi", Matchers.is(-65)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometerId", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createHidden() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());
        hydrometer.setIsActive(false);
        hydrometerRepository.save(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", hydrometer.getToken());
        data.put("angle", "123.12345678");
        data.put("temperature", 101.234);
        data.put("temp_units", "C");
        data.put("battery", "3.123456789");
        data.put("gravity", "1.123456");
        data.put("interval", 100);
        data.put("RSSI", -65);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.angle", Matchers.is(123.12345678)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature", Matchers.is(101.234)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.battery", Matchers.is(3.123456789)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gravity.specificGravity", Matchers.is(1.123456)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.interval", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rssi", Matchers.is(-65)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometerId", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createNoBeerAssignedFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", hydrometer.getToken());
        data.put("angle", "123.12345678");
        data.put("temperature", 101.234);
        data.put("temp_units", "C");
        data.put("battery", "3.123456789");
        data.put("gravity", "123.12345678");
        data.put("interval", 100);
        data.put("RSSI", -65);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", "");
        data.put("angle", "-123.12345678");
        data.put("temperature", -101.234);
        data.put("temp_units", "CELSIUS");
        data.put("battery", "33.123456789");
        data.put("gravity", "213.12345678");
        data.put("interval", null);
        data.put("RSSI", 123);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.VALIDATION_ERROR.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'token')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'angle')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'temperature')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'temp_units')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'battery')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'gravity')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'interval')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'RSSI')]").exists());
    }

    @Test
    void createRequestParsingFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", true);
        data.put("angle", true);
        data.put("temperature", "abcd");
        data.put("temp_units", 42);
        data.put("battery", "dbd");
        data.put("gravity", "gravity");
        data.put("interval", "int");
        data.put("RSSI", 13.3333);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.PARSING_ERROR.toString()))
                );
    }

    @Test
    void createIncorrectTokenFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("token", "wrong-token");
        data.put("angle", "123.12345678");
        data.put("temperature", 101.234);
        data.put("temp_units", "C");
        data.put("battery", "3.123456789");
        data.put("gravity", "123.12345678");
        data.put("interval", 100);
        data.put("RSSI", -65);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void readAllUser() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        Hydrometer hydrometer = createHydrometer(beer);
        hydrometer.setCreatedBy(getAdmin());
        hydrometer.setToken(hydrometer.getToken() + "test2");
        hydrometerRepository.save(hydrometer);
        createMeasurement(hydrometer, beer);

        beer = createBeer();
        hydrometer = createHydrometer(beer);
        Measurement measurement = createMeasurement(hydrometer, beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(measurement.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].angle", Matchers.hasItem(measurement.getAngle().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].temperature", Matchers.hasItem(measurement.getTemperature().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].battery", Matchers.hasItem(measurement.getBattery().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].gravity.specificGravity", Matchers.hasItem(measurement.getSpecificGravity().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].interval", Matchers.hasItem(measurement.getInterval())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].rssi", Matchers.hasItem(measurement.getRssi())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometerId", Matchers.hasItem(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].beerId", Matchers.hasItem(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hidden", Matchers.hasItem(measurement.isHidden())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readAllAdmin() throws Exception {
        Beer beer = createBeer();
        Hydrometer hydrometer = createHydrometer(beer);
        Measurement measurement = createMeasurement(hydrometer, beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(measurement.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].angle", Matchers.hasItem(measurement.getAngle().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].temperature", Matchers.hasItem(measurement.getTemperature().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].battery", Matchers.hasItem(measurement.getBattery().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].gravity.specificGravity", Matchers.hasItem(measurement.getSpecificGravity().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].interval", Matchers.hasItem(measurement.getInterval())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].rssi", Matchers.hasItem(measurement.getRssi())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometerId", Matchers.hasItem(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].beerId", Matchers.hasItem(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hidden", Matchers.hasItem(measurement.isHidden())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readAllUnauthenticatedFail() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void readOneUser() throws Exception {
        Beer beer = createBeer();
        Hydrometer hydrometer = createHydrometer(beer);
        Measurement measurement = createMeasurement(hydrometer, beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(measurement.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.angle", Matchers.is(measurement.getAngle().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature", Matchers.is(measurement.getTemperature().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.battery", Matchers.is(measurement.getBattery().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gravity.specificGravity", Matchers.is(measurement.getSpecificGravity().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.interval", Matchers.is(measurement.getInterval())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rssi", Matchers.is(measurement.getRssi())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometerId", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(measurement.isHidden())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneAdmin() throws Exception {
        Beer beer = createBeer();
        Hydrometer hydrometer = createHydrometer(beer);
        Measurement measurement = createMeasurement(hydrometer, beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(measurement.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.angle", Matchers.is(measurement.getAngle().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature", Matchers.is(measurement.getTemperature().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.battery", Matchers.is(measurement.getBattery().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gravity.specificGravity", Matchers.is(measurement.getSpecificGravity().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.interval", Matchers.is(measurement.getInterval())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rssi", Matchers.is(measurement.getRssi())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometerId", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(measurement.isHidden())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
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
    void readOneUnauthorizedFail() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        Hydrometer hydrometer = createHydrometer(beer);
        hydrometer.setCreatedBy(getAdmin());
        hydrometerRepository.save(hydrometer);
        Measurement measurement = createMeasurement(hydrometer, beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        Beer beer = createBeer();
        Hydrometer hydrometer = createHydrometer(beer);
        Measurement measurement = createMeasurement(hydrometer, beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(measurement.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);
        Beer beerNew = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", beerNew.getId());
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(measurement.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(beerNew.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(true)));
    }

    @Test
    void updateAdmin() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);
        Beer beerNew = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", beerNew.getId());
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(measurement.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerId", Matchers.is(beerNew.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hidden", Matchers.is(true)));
    }

    @Test
    void updateValidationFail() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", "abcd-123");
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.VALIDATION_ERROR.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'beerId')]").exists());
    }

    @Test
    void updateRequestParsingFail() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);
        Beer beerNew = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", beerNew.getId());
        data.put("hidden", "no");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.PARSING_ERROR.toString())
                ));
    }

    @Test
    void updateNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", createBeer().getId());
        data.put("hidden", true);

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
    void updateBeerNotFoundFail() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", UUID.randomUUID());
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateBeerInaccessibleFail() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);
        Beer beerNew = createBeer();
        beerNew.setCreatedBy(getAdmin());
        beerRepository.save(beerNew);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", beerNew.getId());
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthorizedFail() throws Exception {
        Beer beerPrev = createBeer();
        beerPrev.setCreatedBy(getAdmin());
        beerRepository.save(beerPrev);
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);
        Beer beerNew = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", beerNew.getId());
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Beer beerPrev = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beerPrev), beerPrev);
        Beer beerNew = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("beerId", beerNew.getId());
        data.put("hidden", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(measurement.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUser() throws Exception {
        Beer beer = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beer), beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Beer beer = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beer), beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(measurement.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Beer beer = createBeer();
        Measurement measurement = createMeasurement(createHydrometer(beer), beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(measurement.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorizedFail() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);
        Measurement measurement = createMeasurement(createHydrometer(beer), beer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(measurement.getId()))
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
     * Helper method to create a dummy beer instance
     *
     * @return beer instance
     */
    private Beer createBeer() {
        Beer beer = new Beer();
        beer.setName("Test beer");
        beer.setCreatedBy(getUser());
        beer.setState(BrewStateEnum.PLANNING);
        beer.setCreatedAt(LocalDateTime.now());
        beer.setUpdatedAt(LocalDateTime.now());

        return beerRepository.save(beer);
    }

    /**
     * Helper method to create a dummy hydrometer instance
     *
     * @param beer beer to assign to hydrometer
     * @return hydrometer instance
     */
    private Hydrometer createHydrometer(Beer beer) {
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setName("iSpindel");
        hydrometer.setToken("hydro-token");
        hydrometer.setAssignedBeer(createBeer());
        hydrometer.setIsActive(true);
        hydrometer.setAssignedBeer(beer);
        hydrometer.setCreatedBy(getUser());
        hydrometer.setCreatedAt(LocalDateTime.now());
        hydrometer.setUpdatedAt(LocalDateTime.now());

        return hydrometerRepository.save(hydrometer);
    }

    /**
     * Helper method to create a dummy measurement instance
     *
     * @param hydrometer hydrometer to be assigned to
     * @param beer       beer to be assigned to
     * @return measurement instance
     */
    private Measurement createMeasurement(Hydrometer hydrometer, Beer beer) {
        Measurement measurement = new Measurement();
        measurement.setAngle(BigDecimal.valueOf(58.9));
        measurement.setTemperature(BigDecimal.valueOf(20.12));
        measurement.setBattery(BigDecimal.valueOf(1.1132));
        measurement.setSpecificGravity(BigDecimal.valueOf(1.1001));
        measurement.setInterval(300);
        measurement.setRssi(-70);
        measurement.setHydrometer(hydrometer);
        measurement.setBeer(beer);
        measurement.setIsHidden(false);
        measurement.setCreatedAt(LocalDateTime.now());
        measurement.setUpdatedAt(LocalDateTime.now());

        return measurementRepository.save(measurement);
    }

    private String getUri() {
        return "/api/v0/measurements";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }

}
