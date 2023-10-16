package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findFirstByEmailOrUsername() {
        // init
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("mail@gmail.com");
        user2.setUsername(null);

        // persist
        userRepository.save(user1);
        userRepository.save(user2);

        // test
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("dummy@example.com", "uname").isPresent());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("other@mail.com", null).isEmpty());
        Assertions.assertTrue(userRepository.getFirstByEmailOrUsername("other@mail.com", "dummyUser").isPresent());
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
