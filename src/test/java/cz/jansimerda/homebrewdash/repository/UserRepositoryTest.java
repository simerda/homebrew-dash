package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findFirstByEmailOrUsername() {
        // init
        User user = createUser();

        // persist
        userRepository.save(user);

        // test
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("dummy@example.com", "uname").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("other@mail.com", "dummyUser").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("other@mail.com", "uname").isEmpty());
    }

    @Test
    void findFirstByEmailOrUsernameExceptId() {
        // init
        User user = createUser();

        // persist
        userRepository.save(user);

        // test different id
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("dummy@example.com", "uname", UUID.randomUUID()).isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("other@mail.com", "dummyUser", UUID.randomUUID()).isPresent());

        // test same id
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("dummy@example.com", "uname", user.getId()).isEmpty());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("other@mail.com", "dummyUser", user.getId()).isEmpty());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("other@mail.com", "uname", user.getId()).isEmpty());
    }

    @Test
    void findFirstByEmail() {
        // init
        User user = createUser();

        // persist
        userRepository.save(user);
        // test
        Assertions.assertTrue(userRepository.getFirstByEmail("dummy@example.com").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmail("other@mail.com").isEmpty());
    }

    @Test
    void findFirstByUsername() {
        // init
        User user = createUser();

        // persist
        userRepository.save(user);
        // test
        Assertions.assertTrue(userRepository.getFirstByUsername("dummyUser").isPresent());
        Assertions.assertTrue(userRepository.getFirstByUsername("uname").isEmpty());
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
