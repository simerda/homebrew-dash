package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.business.MerossService;
import cz.jansimerda.homebrewdash.exception.exposed.ExposedExceptionTypeEnum;
import cz.jansimerda.homebrewdash.exception.internal.meross.DeviceNotFoundMerossException;
import cz.jansimerda.homebrewdash.exception.internal.meross.GeneralMerossException;
import cz.jansimerda.homebrewdash.exception.internal.meross.InvalidCredentialsMerossException;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import cz.jansimerda.homebrewdash.repository.ThermostatRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class ThermostatControllerTest extends AbstractControllerTest {

    @MockBean
    MerossService merossService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ThermostatRepository thermostatRepository;
    @Autowired
    private HydrometerRepository hydrometerRepository;

    @AfterEach
    @Override
    protected void tearDown() {
        thermostatRepository.deleteAll();
        hydrometerRepository.deleteAll();
        super.tearDown();
    }

    @Test
    void create() throws Exception {
        Hydrometer hydrometer = createHydrometer();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", hydrometer.getId());

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("My thermostat")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", Matchers.is("my smart plug")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("john.doe@mail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.heating", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.on", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(ThermostatStateEnum.READY.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.name", Matchers.is(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.assignedBeerId", Matchers.is(hydrometer.getAssignedBeer().map(Beer::getId).map(UUID::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.active", Matchers.is(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.createdById", Matchers.is(hydrometer.getCreatedBy().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.createdAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastSuccessAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastFailAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createRequestParsingFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", 1.23);
        data.put("deviceName", 1.23);
        data.put("email", 1.23);
        data.put("password", 1.23);
        data.put("heating", "yes");
        data.put("active", "no");
        data.put("hydrometerId", 4);

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
        data.put("name", "a");
        data.put("deviceName", null);
        data.put("email", "invalid.mail");
        data.put("password", "p".repeat(151));
        data.put("heating", false);
        data.put("active", false);
        data.put("hydrometerId", "not UUID");

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'deviceName')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'email')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'password')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'hydrometerId')]").exists());
    }

    @Test
    void createHydrometerNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", UUID.randomUUID());

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
    void createInaccessibleHydrometerFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setCreatedBy(getAdmin());
        hydrometerRepository.save(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", hydrometer.getId());

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
    void createDeviceNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        Mockito.doThrow(new DeviceNotFoundMerossException("")).when(merossService)
                .turnOff("john.doe@mail.com", "1SecurePass++", "my smart plug");

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
                        Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString()))
                );
    }

    @Test
    void createInvalidCredentialsFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        Mockito.doThrow(new InvalidCredentialsMerossException("")).when(merossService)
                .turnOff("john.doe@mail.com", "1SecurePass++", "my smart plug");

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
                        Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString()))
                );
    }

    @Test
    void createServiceErrorFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        Mockito.doThrow(new GeneralMerossException("")).when(merossService)
                .turnOff("john.doe@mail.com", "1SecurePass++", "my smart plug");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.SERVICE_UNAVAILABLE.toString()))
                );
    }

    @Test
    void createUnauthenticatedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

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
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);
        thermostat.setCreatedBy(getAdmin());
        thermostatRepository.save(thermostat);
        thermostat = createThermostat(hydrometer);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(thermostat.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(thermostat.getName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].deviceName", Matchers.hasItem(thermostat.getDeviceName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email", Matchers.hasItem(thermostat.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].heating", Matchers.hasItem(thermostat.isHeating())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].active", Matchers.hasItem(thermostat.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].on", Matchers.hasItem(thermostat.isPoweredOn())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].state", Matchers.hasItem(thermostat.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.id", Matchers.hasItem(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.name", Matchers.hasItem(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.token", Matchers.hasItem(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.assignedBeerId", Matchers.hasItem(hydrometer.getAssignedBeer().map(Beer::getId).map(UUID::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.active", Matchers.hasItem(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.createdById", Matchers.hasItem(hydrometer.getCreatedBy().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].lastSuccessAt", Matchers.hasItem(thermostat.getLastSuccessAt().map(LocalDateTime::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].lastFailAt", Matchers.hasItem(thermostat.getLastFailAt().map(LocalDateTime::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdById", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readAllAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(thermostat.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.hasItem(thermostat.getName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].deviceName", Matchers.hasItem(thermostat.getDeviceName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email", Matchers.hasItem(thermostat.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].heating", Matchers.hasItem(thermostat.isHeating())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].active", Matchers.hasItem(thermostat.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].on", Matchers.hasItem(thermostat.isPoweredOn())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].state", Matchers.hasItem(thermostat.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.id", Matchers.hasItem(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.name", Matchers.hasItem(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.token", Matchers.hasItem(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.assignedBeerId", Matchers.hasItem(hydrometer.getAssignedBeer().map(Beer::getId).map(UUID::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.active", Matchers.hasItem(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.createdById", Matchers.hasItem(hydrometer.getCreatedBy().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.createdAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].hydrometer.updatedAt", Matchers.hasItem(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].lastSuccessAt", Matchers.hasItem(thermostat.getLastSuccessAt().map(LocalDateTime::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].lastFailAt", Matchers.hasItem(thermostat.getLastFailAt().map(LocalDateTime::toString).orElse(null))))
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
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(thermostat.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(thermostat.getName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", Matchers.is(thermostat.getDeviceName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(thermostat.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.heating", Matchers.is(thermostat.isHeating())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(thermostat.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.on", Matchers.is(thermostat.isPoweredOn())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(thermostat.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.name", Matchers.is(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.assignedBeerId", Matchers.is(hydrometer.getAssignedBeer().map(Beer::getId).map(UUID::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.active", Matchers.is(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.createdById", Matchers.is(hydrometer.getCreatedBy().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.createdAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastSuccessAt", Matchers.is(thermostat.getLastSuccessAt().map(LocalDateTime::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastFailAt", Matchers.is(thermostat.getLastFailAt().map(LocalDateTime::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.is(Matchers.matchesRegex(".+"))));
    }

    @Test
    void readOneAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(thermostat.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(thermostat.getName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", Matchers.is(thermostat.getDeviceName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(thermostat.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.heating", Matchers.is(thermostat.isHeating())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(thermostat.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.on", Matchers.is(thermostat.isPoweredOn())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(thermostat.getState().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.id", Matchers.is(hydrometer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.name", Matchers.is(hydrometer.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.token", Matchers.is(hydrometer.getToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.assignedBeerId", Matchers.is(hydrometer.getAssignedBeer().map(Beer::getId).map(UUID::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.active", Matchers.is(hydrometer.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.createdById", Matchers.is(hydrometer.getCreatedBy().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.createdAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer.updatedAt", Matchers.is(Matchers.matchesRegex(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastSuccessAt", Matchers.is(thermostat.getLastSuccessAt().map(LocalDateTime::toString).orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastFailAt", Matchers.is(thermostat.getLastFailAt().map(LocalDateTime::toString).orElse(null))))
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
    void readOneUnauthorizedUser() throws Exception {
        Thermostat thermostat = createThermostat(null);
        thermostat.setCreatedBy(getAdmin());
        thermostatRepository.save(thermostat);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        Thermostat thermostat = createThermostat(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(thermostat.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(thermostat.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("My thermostat")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", Matchers.is("my smart plug")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("john.doe@mail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.heating", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.on", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(ThermostatStateEnum.WAITING_FOR_HYDROMETER.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastSuccessAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastFailAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateAdmin() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(thermostat.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("My thermostat")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", Matchers.is("my smart plug")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("john.doe@mail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.heating", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.on", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state", Matchers.is(ThermostatStateEnum.WAITING_FOR_HYDROMETER.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hydrometer", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastSuccessAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastFailAt", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdById", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void updateValidationFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "a");
        data.put("deviceName", null);
        data.put("email", "invalid.mail");
        data.put("password", "p".repeat(151));
        data.put("heating", false);
        data.put("active", false);
        data.put("hydrometerId", "not UUID");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.VALIDATION_ERROR.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'name')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'deviceName')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'email')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'password')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'hydrometerId')]").exists());
    }

    @Test
    void updateRequestParsingFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", 1.23);
        data.put("deviceName", 1.23);
        data.put("email", 1.23);
        data.put("password", 1.23);
        data.put("heating", "yes");
        data.put("active", "no");
        data.put("hydrometerId", 4);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
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
    void updateHydrometerNotFoundFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", UUID.randomUUID());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateInaccessibleHydrometerFail() throws Exception {
        Thermostat thermostat = createThermostat(null);
        Hydrometer hydrometer = createHydrometer();
        hydrometer.setCreatedBy(getAdmin());
        hydrometerRepository.save(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", hydrometer.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateDeviceNotFoundFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        Mockito.doThrow(new DeviceNotFoundMerossException("")).when(merossService)
                .turnOff("john.doe@mail.com", "1SecurePass++", "my smart plug");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString()))
                );
    }

    @Test
    void updateInvalidCredentialsFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        Mockito.doThrow(new InvalidCredentialsMerossException("")).when(merossService)
                .turnOff("john.doe@mail.com", "1SecurePass++", "my smart plug");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString()))
                );
    }

    @Test
    void updateServiceErrorFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        Mockito.doThrow(new GeneralMerossException("")).when(merossService)
                .turnOff("john.doe@mail.com", "1SecurePass++", "my smart plug");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.type",
                        Matchers.is(ExposedExceptionTypeEnum.SERVICE_UNAVAILABLE.toString()))
                );
    }

    @Test
    void updateNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

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
        Thermostat thermostat = createThermostat(null);
        thermostat.setCreatedBy(getAdmin());
        thermostatRepository.save(thermostat);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        Hydrometer hydrometer = createHydrometer();
        Thermostat thermostat = createThermostat(hydrometer);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "My thermostat");
        data.put("deviceName", "my smart plug");
        data.put("email", "john.doe@mail.com");
        data.put("password", "1SecurePass++");
        data.put("heating", true);
        data.put("active", true);
        data.put("hydrometerId", null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(thermostat.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUser() throws Exception {
        Thermostat thermostat = createThermostat(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteAdmin() throws Exception {
        Thermostat thermostat = createThermostat(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        Thermostat thermostat = createThermostat(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(thermostat.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorizedFail() throws Exception {
        Thermostat thermostat = createThermostat(null);
        thermostat.setCreatedBy(getAdmin());
        thermostatRepository.save(thermostat);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(thermostat.getId()))
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
    void deleteServiceUnavailableFail() throws Exception {
        Thermostat thermostat = createThermostat(null);

        Mockito.doThrow(new GeneralMerossException("")).when(merossService)
                .turnOff(thermostat.getEmail(), thermostat.getPassword(), thermostat.getDeviceName());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(getUri(thermostat.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
    }

    /**
     * Helper method to create a dummy hydrometer instance
     *
     * @return hydrometer instance
     */
    private Hydrometer createHydrometer() {
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setName("iSpindel");
        hydrometer.setToken("hydro-token");
        hydrometer.setIsActive(true);
        hydrometer.setAssignedBeer(null);
        hydrometer.setCreatedBy(getUser());
        hydrometer.setCreatedAt(LocalDateTime.now());
        hydrometer.setUpdatedAt(LocalDateTime.now());

        return hydrometerRepository.save(hydrometer);
    }

    private Thermostat createThermostat(Hydrometer hydrometer) {
        Thermostat thermostat = new Thermostat();
        thermostat.setName("Thermostat #1");
        thermostat.setDeviceName("Smart Plug");
        thermostat.setEmail("my@mail.com");
        thermostat.setPassword("mypass");
        thermostat.setIsHeating(true);
        thermostat.setIsActive(true);
        thermostat.setIsPoweredOn(true);
        thermostat.setHydrometer(hydrometer);
        thermostat.setState(ThermostatStateEnum.ACTIVE);
        thermostat.setCreatedBy(getUser());
        thermostat.setCreatedAt(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());

        return thermostatRepository.save(thermostat);
    }

    private String getUri() {
        return "/api/v1/thermostats";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
