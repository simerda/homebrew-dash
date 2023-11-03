package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.model.User;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
class MaltChangeControllerTest extends AbstractControllerTest {

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
    void createUserAuth() throws Exception {
        User user = getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 30);
        data.put("changeGrams", 500);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(30)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createAdminAuth() throws Exception {
        User user = getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 30);
        data.put("changeGrams", 500);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(30)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("maltId", "321");
        data.put("colorEbc", 200);
        data.put("changeGrams", 0);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'userId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'maltId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'colorEbc')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'changeGrams')]").exists());
    }

    @Test
    void createInsufficientStockFail() throws Exception {
        User user = getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 30);
        data.put("changeGrams", -500);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createMaltNotFoundFail() throws Exception {
        User user = getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", UUID.randomUUID());
        data.put("colorEbc", 30);
        data.put("changeGrams", 500);

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
    void createUserUnauthorised() throws Exception {
        User user = getAdmin();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 30);
        data.put("changeGrams", 500);

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
    void createUserUnauthenticated() throws Exception {
        User user = getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 30);
        data.put("changeGrams", 500);

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
        MaltChange change = createMaltChange();
        change.setUser(getAdmin());
        maltChangeRepository.save(change);

        change = createMaltChange();
        Malt malt = change.getMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].userId", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].changeGrams", Matchers.hasItem(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].colorEbc", Matchers.hasItem(change.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.id", Matchers.hasItem(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.name", Matchers.hasItem(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.manufacturerName", Matchers.hasItem(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.createdByUserId", Matchers.hasItem(getUser().getId().toString())));
    }

    @Test
    void readAllAdmin() throws Exception {
        MaltChange change = createMaltChange();
        Malt malt = change.getMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateAdmin())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].userId", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].changeGrams", Matchers.hasItem(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].colorEbc", Matchers.hasItem(change.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.id", Matchers.hasItem(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.name", Matchers.hasItem(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.manufacturerName", Matchers.hasItem(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].malt.createdByUserId", Matchers.hasItem(getUser().getId().toString())));
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
        MaltChange change = createMaltChange();
        Malt malt = change.getMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(change.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneAdmin() throws Exception {
        MaltChange change = createMaltChange();
        Malt malt = change.getMalt();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(change.getColorEbc().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneUnauthorisedFail() throws Exception {
        MaltChange change = createMaltChange();
        change.setUser(getAdmin());
        maltChangeRepository.save(change);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        MaltChange change = createMaltChange();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(change.getId()))
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
        MaltChange change = createMaltChange();
        User user = change.getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 32);
        data.put("changeGrams", 120);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(32)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(120)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateAdmin() throws Exception {
        MaltChange change = createMaltChange();
        User user = change.getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 32);
        data.put("changeGrams", 120);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.id", Matchers.is(malt.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.name", Matchers.is(malt.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.manufacturerName", Matchers.is(malt.getManufacturerName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.malt.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.colorEbc", Matchers.is(32)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(120)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateValidationFail() throws Exception {
        MaltChange change = createMaltChange();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("maltId", "321");
        data.put("colorEbc", -4);
        data.put("changeGrams", 0);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'userId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'maltId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'colorEbc')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'changeGrams')]").exists());
    }

    @Test
    void updateInsufficientStockFail() throws Exception {
        Malt malt = createMalt();
        MaltChange changeToUpdate = createMaltChange();
        changeToUpdate.setMalt(malt);
        changeToUpdate.setChangeGrams(100);
        maltChangeRepository.save(changeToUpdate);

        MaltChange change = createMaltChange();
        change.setMalt(malt);
        change.setChangeGrams(-100);
        maltChangeRepository.save(change);
        User user = changeToUpdate.getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", null);
        data.put("changeGrams", 50);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(changeToUpdate.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUnauthorised() throws Exception {
        MaltChange change = createMaltChange();
        change.setUser(getAdmin());
        maltChangeRepository.save(change);
        User user = change.getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 32);
        data.put("changeGrams", 120);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticated() throws Exception {
        MaltChange change = createMaltChange();
        User user = change.getUser();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 32);
        data.put("changeGrams", 120);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUserNotFound() throws Exception {
        MaltChange change = createMaltChange();
        Malt malt = createMalt();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", UUID.randomUUID());
        data.put("maltId", malt.getId());
        data.put("colorEbc", 32);
        data.put("changeGrams", 120);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateMaltNotFound() throws Exception {
        MaltChange change = createMaltChange();
        User user = change.getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("maltId", UUID.randomUUID());
        data.put("colorEbc", 32);
        data.put("changeGrams", 120);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    void deleteUser() throws Exception {
        MaltChange change = createMaltChange();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        MaltChange change = createMaltChange();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        MaltChange change = createMaltChange();

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
        MaltChange change = createMaltChange();
        change.setUser(getAdmin());
        maltChangeRepository.save(change);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(change.getId()))
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
    void deleteWouldCauseInsufficientStockFail() throws Exception {
        Malt malt = createMalt();
        MaltChange changeToDelete = createMaltChange();
        changeToDelete.setMalt(malt);
        changeToDelete.setChangeGrams(100);
        maltChangeRepository.save(changeToDelete);

        MaltChange change = createMaltChange();
        change.setMalt(malt);
        change.setChangeGrams(-100);
        maltChangeRepository.save(change);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(changeToDelete.getId()))
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

    /**
     * Create a dummy MaltChange entity and persist it into the DB
     *
     * @return MaltChange entity
     */
    private MaltChange createMaltChange() {
        MaltChange change = new MaltChange();
        change.setChangeGrams(100);
        change.setColorEbc(20);
        change.setUser(getUser());
        change.setMalt(createMalt());

        return maltChangeRepository.save(change);
    }

    private String getUri() {
        return "/api/v0/malts/changes";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
