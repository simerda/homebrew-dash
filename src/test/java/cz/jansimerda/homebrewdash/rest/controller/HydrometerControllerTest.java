package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.exception.exposed.ExposedExceptionTypeEnum;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
class HydrometerControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HydrometerRepository hydrometerRepository;

    @Autowired
    private BeerRepository beerRepository;

    @AfterEach
    @Override
    protected void tearDown() {
        hydrometerRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void create() throws Exception {
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", beer.getId());
        data.put("active", true);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("My iSpindel")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignedBeerId", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createRequestParsingFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("active", "yes");

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
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.PARSING_ERROR.toString()))
                );
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "      ");
        data.put("assignedBeerId", 123);
        data.put("active", true);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.VALIDATION_ERROR.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'name')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'assignedBeerId')]").exists());
    }

    @Test
    void createBeerNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", UUID.randomUUID());
        data.put("active", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createInaccessibleBeerFail() throws Exception {
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", beer.getId());
        data.put("active", true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createUnauthenticatedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", null);
        data.put("active", true);

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
        Beer adminBeer = createBeer();
        adminBeer.setCreatedBy(getAdmin());
        Hydrometer hydrometer = createHydrometer(adminBeer);
        hydrometer.setCreatedBy(getAdmin());
        hydrometer.setToken(hydrometer.getToken() + "test2");
        hydrometerRepository.save(hydrometer);

        hydrometer = createHydrometer(createBeer());

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].token", Matchers.hasItem(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].assignedBeerId", Matchers.hasItem(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].active", Matchers.hasItem(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdById", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readAllAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].token", Matchers.hasItem(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].assignedBeerId", Matchers.hasItem(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].active", Matchers.hasItem(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdById", Matchers.hasItem(getUser().getId().toString())))
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
        Hydrometer hydrometer = createHydrometer(createBeer());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignedBeerId", Matchers.is(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readOneAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignedBeerId", Matchers.is(hydrometer.getAssignedBeer().map(b -> b.getId().toString()).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(Matchers.matchesRegex(".+"))));
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

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(hydrometer.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", beer.getId());
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("My iSpindel")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignedBeerId", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);
        Beer beer = createBeer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", beer.getId());
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("My iSpindel")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignedBeerId", Matchers.is(beer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateValidationFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "    ");
        data.put("assignedBeerId", 12.333);
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.VALIDATION_ERROR.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'name')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'assignedBeerId')]").exists());
    }

    @Test
    void updateRequestParsingFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "    ");
        data.put("assignedBeerId", 12.333);
        data.put("active", "nope");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
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
    void updateBeerNotFoundFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", UUID.randomUUID());
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateBeerNotAccessibleFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);
        Beer beer = createBeer();
        beer.setCreatedBy(getAdmin());
        beerRepository.save(beer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", beer.getId());
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
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
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", null);
        data.put("active", false);

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
    void updateUnauthorisedFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);
        hydrometer.setCreatedBy(getAdmin());
        hydrometerRepository.save(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", null);
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(null);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My iSpindel");
        data.put("assignedBeerId", null);
        data.put("active", false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hydrometer.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUser() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hydrometer.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Hydrometer hydrometer = createHydrometer(createBeer());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hydrometer.getId()))
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
        Hydrometer hydrometer = createHydrometer(beer);
        hydrometer.setCreatedBy(getAdmin());
        hydrometerRepository.save(hydrometer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hydrometer.getId()))
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

    private String getUri() {
        return "/api/v0/hydrometers";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
