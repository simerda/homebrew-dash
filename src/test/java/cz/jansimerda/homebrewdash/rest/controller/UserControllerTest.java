package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.exception.exposed.ExposedExceptionTypeEnum;
import cz.jansimerda.homebrewdash.model.User;
import org.hamcrest.Matchers;
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
class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "mail@example.com");
        data.put("password", "secret123*");
        data.put("username", "myUsername");
        data.put("firstName", "John");
        data.put("surname", "Doe");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("mail@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("myUsername")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.is("Doe")));
    }

    @Test
    void createAuthenticatedAdmin() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "mail@example.com");
        data.put("password", "secret123*");
        data.put("username", "myUsername");
        data.put("firstName", "John");
        data.put("surname", "Doe");

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("mail@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("myUsername")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.is("Doe")));
    }

    @Test
    void createAuthenticatedUserFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "mail@example.com");
        data.put("password", "secret123*");
        data.put("username", "myUsername");
        data.put("firstName", "John");
        data.put("surname", "Doe");

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
    void createValidationFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "mailexample.com");
        data.put("password", "secret");
        data.put("username", "my Username");
        data.put("firstName", "Joh n");
        data.put("surname", "Doe");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'email')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'password')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'username')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.fieldName == 'firstName')]").exists());
    }

    @Test
    void createConditionsNotMetFail() throws Exception {
        User dummy = createDummyUser();
        this.userRepository.save(dummy);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", dummy.getEmail());
        data.put("password", "secret123*");
        data.put("username", "uname");
        data.put("firstName", null);
        data.put("surname", null);

        // test duplicate mail
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString())));

        // test duplicate username
        data.replace("email", "unique@mail.com");
        data.replace("username", dummy.getUsername());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(getUri())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString())));
    }

    @Test
    void readAllUser() throws Exception {
        User user = getUser();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email", Matchers.hasItem(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].username", Matchers.hasItem(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].firstName", Matchers.hasItem(user.getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].surname", Matchers.hasItem(user.getSurname().orElse(null))));
    }

    @Test
    void readAllAdmin() throws Exception {
        User user = getAdmin();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri())
                                .header(AUTHORIZATION, authenticateAdmin())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email", Matchers.hasItem(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].username", Matchers.hasItem(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].firstName", Matchers.hasItem(user.getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].surname", Matchers.hasItem(user.getSurname().orElse(null))));
    }

    @Test
    void readOneUser() throws Exception {
        User user = getUser();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(user.getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.is(user.getSurname().orElse(null))));
    }

    @Test
    void readOneAdmin() throws Exception {
        User user = getUser();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(user.getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.is(user.getSurname().orElse(null))));
    }

    @Test
    void readOneUnauthenticatedFail() throws Exception {
        User user = getUser();

        mockMvc.perform(MockMvcRequestBuilders.get(getUri(user.getId())).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void readOneUnauthorizedFail() throws Exception {
        User user = getAdmin();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateUser() throws Exception {
        User user = getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "somemail@example.com");
        data.put("password", "(mypass21)");
        data.put("username", "otherUsername");
        data.put("firstName", "Ada");
        data.put("surname", "Lovelace");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("somemail@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("otherUsername")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is("Ada")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.is("Lovelace")));
    }

    @Test
    void updateAdmin() throws Exception {
        User user = getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "somemail@example.com");
        data.put("password", "(mypass21)");
        data.put("username", "otherUsername");
        data.put("firstName", "Ada");
        data.put("surname", "Lovelace");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateAdmin())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("somemail@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("otherUsername")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is("Ada")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.is("Lovelace")));
    }

    @Test
    void updateConditionsNotMetFail() throws Exception {
        User user = getUser();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", getAdmin().getEmail());
        data.put("username", "otherUsername");
        data.put("password", "(mypass21)");
        data.put("firstName", "Ada");
        data.put("surname", "Lovelace");

        // duplicate mail
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString())));

        // duplicate username
        data.replace("email", "somemail@example.com");
        data.replace("username", getAdmin().getUsername());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(user.getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(ExposedExceptionTypeEnum.CONDITIONS_NOT_MET.toString())));
    }

    @Test
    void updateUnauthenticatedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "somemail@example.com");
        data.put("password", "(mypass21)");
        data.put("username", "otherUsername");
        data.put("firstName", "Ada");
        data.put("surname", "Lovelace");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(getUser().getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateUnauthorizedFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "somemail@example.com");
        data.put("password", "(mypass21)");
        data.put("username", "otherUsername");
        data.put("firstName", "Ada");
        data.put("surname", "Lovelace");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(getUri(getAdmin().getId()))
                                .header(AUTHORIZATION, authenticateUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteUser() throws Exception {
        User user = getUser();

        // delete
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getUri(user.getId()))
                        .header(AUTHORIZATION, authenticateUser())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // ensure deleted
        mockMvc.perform(MockMvcRequestBuilders
                        .get(getUri(user.getId()))
                        .header(AUTHORIZATION, authenticateAdmin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteAdmin() throws Exception {
        User user = getUser();

        // delete
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getUri(user.getId()))
                        .header(AUTHORIZATION, authenticateAdmin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // ensure deleted
        mockMvc.perform(MockMvcRequestBuilders.get(getUri(user.getId()))
                        .header(AUTHORIZATION, authenticateAdmin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteUnauthenticatedFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getUri(getUser().getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void deleteUnauthorizedFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getUri(getAdmin().getId()))
                        .header(AUTHORIZATION, authenticateUser())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private String getUri() {
        return "/api/v1/users";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }
}
