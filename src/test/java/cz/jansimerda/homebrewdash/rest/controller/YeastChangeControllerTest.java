package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.User;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
public class YeastChangeControllerTest extends AbstractControllerTest {

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
    void createUserAuth() throws Exception {
        User user = getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.kind", Matchers.is(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.type", Matchers.is(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expirationDate", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createAdminAuth() throws Exception {
        User user = getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.kind", Matchers.is(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.type", Matchers.is(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expirationDate", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("yeastId", "321");
        data.put("expirationDate", "next month");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'yeastId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'expirationDate')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'changeGrams')]").exists());
    }

    @Test
    void createInsufficientStockFail() throws Exception {
        User user = getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
    void createYeastNotFoundFail() throws Exception {
        User user = getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", UUID.randomUUID());
        data.put("expirationDate", LocalDate.now().toString());
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
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
        YeastChange change = createYeastChange();
        change.setUser(getAdmin());
        yeastChangeRepository.save(change);

        change = createYeastChange();
        Yeast yeast = change.getYeast();

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].expirationDate", Matchers.hasItem(change.getExpirationDate().stream().map(LocalDate::toString).findFirst().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].changeGrams", Matchers.hasItem(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.id", Matchers.hasItem(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.name", Matchers.hasItem(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.kind", Matchers.hasItem(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.type", Matchers.hasItem(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.createdByUserId", Matchers.hasItem(getUser().getId().toString())));
    }

    @Test
    void readAllAdmin() throws Exception {
        YeastChange change = createYeastChange();
        Yeast yeast = change.getYeast();

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].expirationDate", Matchers.hasItem(change.getExpirationDate().stream().map(LocalDate::toString).findFirst().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].changeGrams", Matchers.hasItem(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.id", Matchers.hasItem(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.name", Matchers.hasItem(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.kind", Matchers.hasItem(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.type", Matchers.hasItem(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].yeast.createdByUserId", Matchers.hasItem(getUser().getId().toString())));
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
        YeastChange change = createYeastChange();
        Yeast yeast = change.getYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.kind", Matchers.is(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.type", Matchers.is(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expirationDate", Matchers.is(change.getExpirationDate().stream().map(LocalDate::toString).findFirst().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneAdmin() throws Exception {
        YeastChange change = createYeastChange();
        Yeast yeast = change.getYeast();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(change.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(change.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.kind", Matchers.is(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.type", Matchers.is(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expirationDate", Matchers.is(change.getExpirationDate().stream().map(LocalDate::toString).findFirst().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneUnauthorisedFail() throws Exception {
        YeastChange change = createYeastChange();
        change.setUser(getAdmin());
        yeastChangeRepository.save(change);

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
        YeastChange change = createYeastChange();

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
        YeastChange change = createYeastChange();
        User user = change.getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.kind", Matchers.is(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.type", Matchers.is(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expirationDate", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(120)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateAdmin() throws Exception {
        YeastChange change = createYeastChange();
        User user = change.getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.id", Matchers.is(yeast.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.name", Matchers.is(yeast.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.kind", Matchers.is(yeast.getKind().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.type", Matchers.is(yeast.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.yeast.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expirationDate", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(120)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateValidationFail() throws Exception {
        YeastChange change = createYeastChange();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("yeastId", "321");
        data.put("expirationDate", "last weekend");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'yeastId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'expirationDate')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'changeGrams')]").exists());
    }

    @Test
    void updateInsufficientStockFail() throws Exception {
        Yeast yeast = createYeast();
        YeastChange changeToUpdate = createYeastChange();
        changeToUpdate.setYeast(yeast);
        changeToUpdate.setChangeGrams(100);
        yeastChangeRepository.save(changeToUpdate);

        YeastChange change = createYeastChange();
        change.setYeast(yeast);
        change.setChangeGrams(-100);
        yeastChangeRepository.save(change);
        User user = changeToUpdate.getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", changeToUpdate.getExpirationDate().stream().map(LocalDate::toString).findFirst().orElse(null));
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
        YeastChange change = createYeastChange();
        change.setUser(getAdmin());
        yeastChangeRepository.save(change);
        User user = change.getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
        YeastChange change = createYeastChange();
        User user = change.getUser();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
        YeastChange change = createYeastChange();
        Yeast yeast = createYeast();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", UUID.randomUUID());
        data.put("yeastId", yeast.getId());
        data.put("expirationDate", LocalDate.now().toString());
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
    void updateYeastNotFound() throws Exception {
        YeastChange change = createYeastChange();
        User user = change.getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("yeastId", UUID.randomUUID());
        data.put("expirationDate", LocalDate.now().toString());
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
        YeastChange change = createYeastChange();

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
        YeastChange change = createYeastChange();

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
        YeastChange change = createYeastChange();

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
        YeastChange change = createYeastChange();
        change.setUser(getAdmin());
        yeastChangeRepository.save(change);

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
        Yeast yeast = createYeast();
        YeastChange changeToDelete = createYeastChange();
        changeToDelete.setYeast(yeast);
        changeToDelete.setChangeGrams(100);
        yeastChangeRepository.save(changeToDelete);

        YeastChange change = createYeastChange();
        change.setYeast(yeast);
        change.setChangeGrams(-100);
        yeastChangeRepository.save(change);

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
     * Create a dummy Yeast entity and persist it into the DB
     *
     * @return Yeast entity
     */
    private Yeast createYeast() {
        Yeast yeast = new Yeast();
        yeast.setName("Some yeast name");
        yeast.setKind(YeastKindEnum.LIQUID);
        yeast.setType(YeastTypeEnum.ALE);
        yeast.setCreatedBy(getUser());

        return yeastRepository.save(yeast);
    }

    /**
     * Create a dummy YeastChange entity and persist it into the DB
     *
     * @return YeastChange entity
     */
    private YeastChange createYeastChange() {
        YeastChange change = new YeastChange();
        change.setChangeGrams(100);
        change.setExpirationDate(LocalDate.now().plusYears(1));
        change.setUser(getUser());
        change.setYeast(createYeast());

        return yeastChangeRepository.save(change);
    }

    private String getUri() {
        return "/api/v0/yeasts/changes";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
