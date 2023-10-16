package cz.jansimerda.homebrewdash.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jansimerda.homebrewdash.exception.ExposedExceptionTypeEnum;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.UserRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

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
        this.userRepository.save(createUser());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("email", "dummy@example.com");
        data.put("password", "secret123*");
        data.put("username", null);
        data.put("firstName", null);
        data.put("surname", null);

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
    void readAll() throws Exception {
        User user = this.userRepository.save(createUser());

        mockMvc.perform(MockMvcRequestBuilders.get(getUri()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email", Matchers.hasItem(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].username", Matchers.hasItem(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].firstName", Matchers.hasItem(user.getFirstName().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].surname", Matchers.hasItem(user.getSurname().orElse(null))));
    }

    @Test
    void readOne() throws Exception {
        User user = this.userRepository.save(createUser());

        mockMvc.perform(MockMvcRequestBuilders.get(getUri(user.getId())).accept(MediaType.APPLICATION_JSON))
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
    void update() throws Exception {
        User user = this.userRepository.save(createUser());

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
    void delete() throws Exception {
        User user = this.userRepository.save(createUser());

        // delete
        mockMvc.perform(MockMvcRequestBuilders.delete(getUri(user.getId())).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // ensure deleted
        mockMvc.perform(MockMvcRequestBuilders.get(getUri(user.getId())).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private String getUri() {
        return "/api/v0/users";
    }

    private String getUri(UUID id) {
        return getUri() + "/" + id.toString();
    }

    private User createUser() {
        User user = new User();
        user.setEmail("dummy@example.com");
        user.setPassword("(dummyPassword332)");
        user.setUsername("dummyUser");
        user.setFirstName("Dummy");
        user.setSurname("User");
        user.setIsAdmin(false);

        return user;
    }
}
