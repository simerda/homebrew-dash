package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;
import cz.jansimerda.homebrewdash.repository.YeastChangeRepository;
import cz.jansimerda.homebrewdash.repository.YeastRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
class YeastControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private YeastRepository yeastRepository;

    @Autowired
    private YeastChangeRepository yeastChangeRepository;

    @Override
    @AfterEach
    protected void tearDown() {
        yeastChangeRepository.deleteAll();
        yeastRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void create() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", "Yeast manufacturer name");
        data.put("kind", YeastKindEnum.DRIED.name());
        data.put("type", YeastTypeEnum.ALE.name());

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Yeast name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.is("Yeast manufacturer name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kind", Matchers.is("DRIED")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("ALE")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Ma");
        data.put("manufacturerName", null);
        data.put("kind", "CRYO");
        data.put("type", "PILS");

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'kind')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'type')]").exists());
    }

    @Test
    void createDuplicateFail() throws Exception {
        createYeast();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Some yeast name");
        data.put("manufacturerName", "Some yeast manufacturer name");
        data.put("kind", YeastKindEnum.DRIED.name());
        data.put("type", YeastTypeEnum.ALE.name());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createUnauthenticatedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", "Yeast manufacturer name");
        data.put("kind", YeastKindEnum.DRIED.name());
        data.put("type", YeastTypeEnum.ALE.name());

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
    void readAll() throws Exception {
        Yeast yeast = createYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].manufacturerName", Matchers.hasItem(yeast.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].kind", Matchers.hasItem("DRIED")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].type", Matchers.hasItem("ALE")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdByUserId", Matchers.hasItem(yeast.getCreatedBy().getId().toString())));
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
    void readOne() throws Exception {
        Yeast yeast = createYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.is(yeast.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kind", Matchers.is("DRIED")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("ALE")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(yeast.getCreatedBy().getId().toString())));
    }

    @Test
    void readOneNotFoundFail() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(UUID.randomUUID()))
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        Yeast yeast = createYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(yeast.getId()))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", null);
        data.put("kind", YeastKindEnum.LIQUID.name());
        data.put("type", YeastTypeEnum.LAGER.name());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Yeast name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kind", Matchers.is("LIQUID")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("LAGER")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void updateAdmin() throws Exception {
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", null);
        data.put("kind", YeastKindEnum.LIQUID.name());
        data.put("type", YeastTypeEnum.LAGER.name());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Yeast name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kind", Matchers.is("LIQUID")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is("LAGER")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void updateNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", null);
        data.put("kind", YeastKindEnum.LIQUID.name());
        data.put("type", YeastTypeEnum.LAGER.name());

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
        Yeast yeast = createYeast();
        yeast.setCreatedBy(getAdmin());
        yeastRepository.save(yeast);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", null);
        data.put("kind", YeastKindEnum.LIQUID.name());
        data.put("type", YeastTypeEnum.LAGER.name());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Yeast name");
        data.put("manufacturerName", null);
        data.put("kind", YeastKindEnum.LIQUID.name());
        data.put("type", YeastTypeEnum.LAGER.name());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(yeast.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateDuplicateFail() throws Exception {
        // first yeast
        Yeast yeast = createYeast();
        yeast.setManufacturerName(null);
        yeastRepository.save(yeast);

        // second yeast that will clash with the first after update
        yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Some yeast name");
        data.put("manufacturerName", null);
        data.put("kind", YeastKindEnum.LIQUID.name());
        data.put("type", YeastTypeEnum.LAGER.name());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteUser() throws Exception {
        Yeast yeast = createYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Yeast yeast = createYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Yeast yeast = createYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(yeast.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorisedFail() throws Exception {
        Yeast yeast = createYeast();
        yeast.setCreatedBy(getAdmin());
        yeastRepository.save(yeast);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(yeast.getId()))
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

    @Test
    void deleteAttachedToChangesFail() throws Exception {
        Yeast yeast = createYeast();
        YeastChange change = new YeastChange();
        change.setYeast(yeast);
        change.setUser(getUser());
        change.setChangeGrams(100);
        change.setExpirationDate(LocalDate.now());
        change.setCreatedAt(LocalDateTime.now());
        yeastChangeRepository.save(change);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(yeast.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Create a dummy Yeast entity and persist it into the DB
     *
     * @return Yeast entity
     */
    private Yeast createYeast() {
        Yeast yeast = new Yeast();
        yeast.setName("Some yeast name");
        yeast.setManufacturerName("Some yeast manufacturer name");
        yeast.setKind(YeastKindEnum.DRIED);
        yeast.setType(YeastTypeEnum.ALE);
        yeast.setCreatedBy(getUser());

        return yeastRepository.save(yeast);
    }

    private String getUri() {
        return "/api/v1/yeasts";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
