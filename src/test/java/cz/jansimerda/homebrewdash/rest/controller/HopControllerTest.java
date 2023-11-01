package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.HopChange;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
class HopControllerTest extends AbstractControllerTest {

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
    void create() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Hop name");
        data.put("alphaAcidPercentage", 7.333);
        data.put("betaAcidPercentage", 5.123456);
        data.put("hopStorageIndex", 0.000211111);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Hop name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(7.333)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(5.123456)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hopStorageIndex", Matchers.is(0.000211111)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Ho");
        data.put("alphaAcidPercentage", -1.20);
        data.put("betaAcidPercentage", 100.25);
        data.put("hopStorageIndex", 0.00);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'alphaAcidPercentage')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'betaAcidPercentage')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'hopStorageIndex')]").exists());
    }

    @Test
    void createDuplicateFail() throws Exception {
        createHop();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Some hop name");
        data.put("alphaAcidPercentage", 7.333);
        data.put("betaAcidPercentage", 5.123456);
        data.put("hopStorageIndex", 0.000211111);

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
        data.put("name", "Hop name");
        data.put("alphaAcidPercentage", 7.333);
        data.put("betaAcidPercentage", 5.123456);
        data.put("hopStorageIndex", 0.000211111);

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
        Hop hop = createHop();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].alphaAcidPercentage", Matchers.hasItem(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].betaAcidPercentage", Matchers.hasItem(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hopStorageIndex", Matchers.hasItem(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdByUserId", Matchers.hasItem(hop.getCreatedBy().getId().toString())));
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
        Hop hop = createHop();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(hop.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(hop.getAlphaAcidPercentage().isPresent() ? hop.getAlphaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(hop.getBetaAcidPercentage().isPresent() ? hop.getBetaAcidPercentage().get().doubleValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hopStorageIndex", Matchers.is(hop.getHopStorageIndex().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(hop.getCreatedBy().getId().toString())));
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
        Hop hop = createHop();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(hop.getId()))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Citra");
        data.put("alphaAcidPercentage", "5.5");
        data.put("betaAcidPercentage", "4.4");
        data.put("hopStorageIndex", "0.005");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Citra")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(5.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(4.4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hopStorageIndex", Matchers.is(0.005)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void updateAdmin() throws Exception {
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Citra");
        data.put("alphaAcidPercentage", "5.5");
        data.put("betaAcidPercentage", "4.4");
        data.put("hopStorageIndex", "0.005");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(hop.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Citra")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.alphaAcidPercentage", Matchers.is(5.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.betaAcidPercentage", Matchers.is(4.4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hopStorageIndex", Matchers.is(0.005)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByUserId", Matchers.is(getUser().getId().toString())));
    }

    @Test
    void updateNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Citra");
        data.put("alphaAcidPercentage", "5.5");
        data.put("betaAcidPercentage", "4.4");
        data.put("hopStorageIndex", "0.005");

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
        Hop hop = createHop();
        hop.setCreatedBy(getAdmin());
        hopRepository.save(hop);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Citra");
        data.put("alphaAcidPercentage", "5.5");
        data.put("betaAcidPercentage", "4.4");
        data.put("hopStorageIndex", "0.005");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Hop hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Citra");
        data.put("alphaAcidPercentage", "5.5");
        data.put("betaAcidPercentage", "4.4");
        data.put("hopStorageIndex", "0.005");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hop.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateDuplicateFail() throws Exception {
        // first hop
        Hop hop = createHop();
        hop.setName("Citra");
        hopRepository.save(hop);

        // second hop that will clash with the first after update
        hop = createHop();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Citra");
        data.put("alphaAcidPercentage", "5.5");
        data.put("betaAcidPercentage", "4.4");
        data.put("hopStorageIndex", "0.005");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteUser() throws Exception {
        Hop hop = createHop();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Hop hop = createHop();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hop.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Hop hop = createHop();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hop.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorisedFail() throws Exception {
        Hop hop = createHop();
        hop.setCreatedBy(getAdmin());
        hopRepository.save(hop);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hop.getId()))
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
        Hop hop = createHop();
        HopChange change = new HopChange();
        change.setHop(hop);
        change.setUser(getUser());
        change.setAlphaAcidPercentage(BigDecimal.valueOf(3.2));
        change.setBetaAcidPercentage(BigDecimal.valueOf(4.1));
        change.setHarvestedAt(LocalDate.now());
        change.setChangeGrams(100);
        change.setCreatedAt(LocalDateTime.now());
        hopChangeRepository.save(change);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(hop.getId()))
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

    private String getUri() {
        return "/api/v0/hops";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
