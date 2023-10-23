package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

@DataJpaTest
class UserRepositoryTest extends AbstractTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void getFirstByEmailOrUsername() {
        // init
        User user = createDummyUser();

        // persist
        userRepository.save(user);

        // test
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("duser@mail.com", "uname").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("other@mail.com", "user123").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("other@mail.com", "uname").isEmpty());
    }

    @Test
    void getFirstByEmailOrUsernameExceptId() {
        // init
        User user = createDummyUser();

        // persist
        user = userRepository.save(user);

        // test different id
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("duser@mail.com", "uname", UUID.randomUUID()).isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("other@mail.com", "user123", UUID.randomUUID()).isPresent());

        // test same id
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("duser@mail.com", "uname", user.getId()).isEmpty());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("other@mail.com", "user123", user.getId()).isEmpty());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsernameExceptId("other@mail.com", "uname", user.getId()).isEmpty());
    }

    @Test
    void getFirstByEmail() {
        // init
        User user = createDummyUser();

        // persist
        userRepository.save(user);
        // test
        Assertions.assertTrue(userRepository.getFirstByEmail("duser@mail.com").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmail("other@mail.com").isEmpty());
    }

    @Test
    void getFirstByUsername() {
        // init
        User user = createDummyUser();

        // persist
        userRepository.save(user);
        // test
        Assertions.assertTrue(userRepository.getFirstByUsername("user123").isPresent());
        Assertions.assertTrue(userRepository.getFirstByUsername("uname").isEmpty());
    }
}
