package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.repository.MaltChangeRepository;
import cz.jansimerda.homebrewdash.repository.MaltRepository;
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
class MaltControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaltRepository maltRepository;

    @Autowired
    private MaltChangeRepository maltChangeRepository;

    @Override
    @AfterEach
    protected void tearDown() {
        maltChangeRepository.deleteAll();
        maltRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void create() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Malt name");
        data.put("manufacturerName", "Malt manufacturer name");

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Malt name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.is("Malt manufacturer name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Ma");
        data.put("manufacturerName", null);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'name')]").exists());
    }

    @Test
    void createDuplicateFail() throws Exception {
        createMalt();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Some malt name");
        data.put("manufacturerName", "Some malt manufacturer name");

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
        data.put("name", "Malt name");
        data.put("manufacturerName", "Malt manufacturer name");

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
        Malt malt = createMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].manufacturerName", Matchers.hasItem(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdByUserId", Matchers.hasItem(malt.getCreatedBy().getId().toString())));
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
        Malt malt = createMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(malt.getCreatedBy().getId().toString())));
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
        Malt malt = createMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(malt.getId()))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Malt name");
        data.put("manufacturerName", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Malt name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void updateAdmin() throws Exception {
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Malt name");
        data.put("manufacturerName", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Malt name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.manufacturerName", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void updateNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Malt name");
        data.put("manufacturerName", null);

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
        Malt malt = createMalt();
        malt.setCreatedBy(getAdmin());
        maltRepository.save(malt);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Malt name");
        data.put("manufacturerName", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Malt name");
        data.put("manufacturerName", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(malt.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateDuplicateFail() throws Exception {
        // first malt
        Malt malt = createMalt();
        malt.setManufacturerName(null);
        maltRepository.save(malt);

        // second malt that will clash with the first after update
        malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Some malt name");
        data.put("manufacturerName", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteUser() throws Exception {
        Malt malt = createMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Malt malt = createMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Malt malt = createMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(malt.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorisedFail() throws Exception {
        Malt malt = createMalt();
        malt.setCreatedBy(getAdmin());
        maltRepository.save(malt);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(malt.getId()))
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
        Malt malt = createMalt();
        MaltChange change = new MaltChange();
        change.setMalt(malt);
        change.setUser(getUser());
        change.setChangeGrams(100);
        change.setColorEbc(null);
        change.setCreatedAt(LocalDateTime.now());
        maltChangeRepository.save(change);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(malt.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Create a dummy Malt entity and persist it into the DB
     *
     * @return Malt entity
     */
    private Malt createMalt() {
        Malt malt = new Malt();
        malt.setName("Some malt name");
        malt.setManufacturerName("Some malt manufacturer name");
        malt.setCreatedBy(getUser());

        return maltRepository.save(malt);
    }

    private String getUri() {
        return "/api/v0/malts";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
