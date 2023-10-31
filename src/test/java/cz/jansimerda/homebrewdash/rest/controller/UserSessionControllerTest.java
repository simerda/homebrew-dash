package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.repository.UserSessionRepository;
import org.hamcrest.Matchers;
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
class UserSessionControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Test
    void create() throws Exception {
        User user = getUser();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("password", USER_PASSWORD);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email", Matchers.is(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username", Matchers.is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.firstName", Matchers.is(user.getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.surname", Matchers.is(user.getSurname().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "invalid-mail");
        data.put("password", "somePass123*");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'email')]").exists());
    }

    @Test
    void createMailNotFoundFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "random@mail.com");
        data.put("password", USER_PASSWORD);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createWrongPasswordFail() throws Exception {
        User user = getUser();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("password", "someWrongPass120*");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void readAllUser() throws Exception {
        // active admin session
        UserSession session = new UserSession();
        session.setUser(getAdmin());
        session.setToken("admin-test-token");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        sessionRepository.save(session);

        // expired user session
        session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token1");
        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        sessionRepository.save(session);

        // active user session
        session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token2");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        session = sessionRepository.save(session);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(session.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].expiresAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].token").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.id", Matchers.hasItem(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.email", Matchers.hasItem(getUser().getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.username", Matchers.hasItem(getUser().getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.firstName", Matchers.hasItem(getUser().getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.surname", Matchers.hasItem(getUser().getSurname().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.updatedAt", Matchers.hasItem(Matchers.matchesPattern(".+"))));
    }

    @Test
    void readAllAdmin() throws Exception {
        // expired user session
        UserSession session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token1");
        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        sessionRepository.save(session);

        // active user session
        session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token2");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        sessionRepository.save(session);

        // expired admin session
        session = new UserSession();
        session.setUser(getAdmin());
        session.setToken("admin-test-token");
        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        sessionRepository.save(session);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateAdmin())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", Matchers.hasItem(session.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].expiresAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].token").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.id", Matchers.hasItem(getAdmin().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.email", Matchers.hasItem(getAdmin().getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.username", Matchers.hasItem(getAdmin().getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.firstName", Matchers.hasItem(getAdmin().getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.surname", Matchers.hasItem(getAdmin().getSurname().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.createdAt", Matchers.hasItem(Matchers.matchesPattern(".+"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].user.updatedAt", Matchers.hasItem(Matchers.matchesPattern(".+"))));
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
        // active user session
        UserSession session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        session = sessionRepository.save(session);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(session.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(session.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email", Matchers.is(getUser().getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username", Matchers.is(getUser().getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.firstName", Matchers.is(getUser().getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.surname", Matchers.is(getUser().getSurname().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneAdmin() throws Exception {
        // expired user session
        UserSession session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token");
        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        session = sessionRepository.save(session);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(session.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(session.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id", Matchers.is(getUser().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email", Matchers.is(getUser().getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username", Matchers.is(getUser().getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.firstName", Matchers.is(getUser().getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.surname", Matchers.is(getUser().getSurname().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.updatedAt", Matchers.not(Matchers.emptyString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.createdAt", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        // active user session
        UserSession session = new UserSession();
        session.setUser(getUser());
        session.setToken("user-test-token");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        session = sessionRepository.save(session);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(session.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void readOneUnauthorizedFail() throws Exception {
        // active admin session
        UserSession adminSession = new UserSession();
        adminSession.setUser(getAdmin());
        adminSession.setToken("admin-test-token");
        adminSession.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        adminSession = sessionRepository.save(adminSession);

        // expired user session
        UserSession userSession = new UserSession();
        userSession.setUser(getUser());
        userSession.setToken("user-test-token");
        userSession.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        userSession = sessionRepository.save(userSession);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(adminSession.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(userSession.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private String getUri() {
        return "/api/v0/user-sessions";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
