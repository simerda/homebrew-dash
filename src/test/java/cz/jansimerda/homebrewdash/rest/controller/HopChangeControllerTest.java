package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.HopChangeRepository;
import cz.jansimerda.homebrewdash.repository.HopRepository;
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
class HopChangeControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HopRepository hopRepository;

    @Autowired
    private HopChangeRepository hopChangeRepository;

    @Override
    @AfterEach
    protected void tearDown() {
        hopChangeRepository.deleteAll();
        hopRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void createUserAuth() throws Exception {
        User user = getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(5.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(3.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.harvestedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createAdminAuth() throws Exception {
        User user = getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(5.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(3.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.harvestedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("hopId", "321");
        data.put("alphaAcidPercentage", "-0.05");
        data.put("betaAcidPercentage", 120);
        data.put("harvestedAt", LocalDate.now().plusDays(10).toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'hopId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'alphaAcidPercentage')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'betaAcidPercentage')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'harvestedAt')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'changeGrams')]").exists());
    }

    @Test
    void createInsufficientStockFail() throws Exception {
        User user = getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
    void createHopNotFoundFail() throws Exception {
        User user = getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", UUID.randomUUID());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
        HopChange change = createHopChange();
        change.setUser(getAdmin());
        hopChangeRepository.save(change);

        change = createHopChange();
        Hop hop = change.getHop();

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].alphaAcidPercentage", Matchers.hasItem(change.getAlphaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].betaAcidPercentage", Matchers.hasItem(change.getBetaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].harvestedAt", Matchers.hasItem(change.getHarvestedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].changeGrams", Matchers.hasItem(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.id", Matchers.hasItem(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.name", Matchers.hasItem(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.alphaAcidPercentage", Matchers.hasItem(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.betaAcidPercentage", Matchers.hasItem(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.hopStorageIndex", Matchers.hasItem(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.createdByUserId", Matchers.hasItem(getUser().getId().toString())));
    }

    @Test
    void readAllAdmin() throws Exception {
        HopChange change = createHopChange();
        Hop hop = change.getHop();

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].alphaAcidPercentage", Matchers.hasItem(change.getAlphaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].betaAcidPercentage", Matchers.hasItem(change.getBetaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].harvestedAt", Matchers.hasItem(change.getHarvestedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].changeGrams", Matchers.hasItem(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.id", Matchers.hasItem(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.name", Matchers.hasItem(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.alphaAcidPercentage", Matchers.hasItem(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.betaAcidPercentage", Matchers.hasItem(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.hopStorageIndex", Matchers.hasItem(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hop.createdByUserId", Matchers.hasItem(getUser().getId().toString())));
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
        HopChange change = createHopChange();
        Hop hop = change.getHop();

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(change.getAlphaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(change.getBetaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.harvestedAt", Matchers.is(change.getHarvestedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneAdmin() throws Exception {
        HopChange change = createHopChange();
        Hop hop = change.getHop();

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.createdByUserId", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(change.getAlphaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(change.getBetaAcidPercentage().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.harvestedAt", Matchers.is(change.getHarvestedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(change.getChangeGrams())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneUnauthorisedFail() throws Exception {
        HopChange change = createHopChange();
        change.setUser(getAdmin());
        hopChangeRepository.save(change);

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
        HopChange change = createHopChange();

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
        HopChange change = createHopChange();
        User user = change.getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(5.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(3.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.harvestedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(120)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(change.getCreatedAt().toString())));
    }

    @Test
    void updateAdmin() throws Exception {
        HopChange change = createHopChange();
        User user = change.getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hop.createdByUserId", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(5.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(3.38)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.harvestedAt", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changeGrams", Matchers.is(120)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(change.getCreatedAt().toString())));
    }

    @Test
    void updateValidationFail() throws Exception {
        HopChange change = createHopChange();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("hopId", "321");
        data.put("alphaAcidPercentage", "-0.05");
        data.put("betaAcidPercentage", 120);
        data.put("harvestedAt", LocalDate.now().plusDays(10).toString());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'hopId')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'alphaAcidPercentage')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'betaAcidPercentage')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'harvestedAt')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'changeGrams')]").exists());
    }

    @Test
    void updateInsufficientStockFail() throws Exception {
        Hop hop = createHop();
        HopChange changeToUpdate = createHopChange();
        changeToUpdate.setHop(hop);
        changeToUpdate.setChangeGrams(100);
        hopChangeRepository.save(changeToUpdate);

        HopChange change = createHopChange();
        change.setHop(hop);
        change.setChangeGrams(-100);
        hopChangeRepository.save(change);
        User user = changeToUpdate.getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", changeToUpdate.getAlphaAcidPercentage().toString());
        data.put("betaAcidPercentage", changeToUpdate.getBetaAcidPercentage().doubleValue());
        data.put("harvestedAt", changeToUpdate.getHarvestedAt().toString());
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
        HopChange change = createHopChange();
        change.setUser(getAdmin());
        hopChangeRepository.save(change);
        User user = change.getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
        HopChange change = createHopChange();
        User user = change.getUser();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
        HopChange change = createHopChange();
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", UUID.randomUUID());
        data.put("hopId", hop.getId());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
    void updateHopNotFound() throws Exception {
        HopChange change = createHopChange();
        User user = change.getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("hopId", UUID.randomUUID());
        data.put("alphaAcidPercentage", "5.12");
        data.put("betaAcidPercentage", 3.38);
        data.put("harvestedAt", LocalDate.now().toString());
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
        HopChange change = createHopChange();

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
        HopChange change = createHopChange();

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
        HopChange change = createHopChange();

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
        HopChange change = createHopChange();
        change.setUser(getAdmin());
        hopChangeRepository.save(change);

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
        Hop hop = createHop();
        HopChange changeToDelete = createHopChange();
        changeToDelete.setHop(hop);
        changeToDelete.setChangeGrams(100);
        hopChangeRepository.save(changeToDelete);

        HopChange change = createHopChange();
        change.setHop(hop);
        change.setChangeGrams(-100);
        hopChangeRepository.save(change);

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
     * Create a dummy Hop entity and persist it into the DB
     *
     * @return Hop entity
     */
    private Hop createHop() {
        Hop hop = new Hop();
        hop.setName("Some hop name");
        hop.setAlphaAcidPercentage(BigDecimal.valueOf(7.7));
        hop.setBetaAcidPercentage(BigDecimal.valueOf(3.2));
        hop.setHopStorageIndex(BigDecimal.valueOf(0.000043));
        hop.setCreatedBy(getUser());

        return hopRepository.save(hop);
    }

    /**
     * Create a dummy HopChange entity and persist it into the DB
     *
     * @return HopChange entity
     */
    private HopChange createHopChange() {
        HopChange change = new HopChange();
        change.setChangeGrams(100);
        change.setAlphaAcidPercentage(BigDecimal.valueOf(7.7));
        change.setBetaAcidPercentage(BigDecimal.valueOf(3.2));
        change.setHarvestedAt(LocalDate.now().minusYears(1));
        change.setUser(getUser());
        change.setHop(createHop());

        return hopChangeRepository.save(change);
    }

    private String getUri() {
        return "/api/v0/hops/changes";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
