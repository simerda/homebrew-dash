package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Test
    void create() {
        User user = createUser();
        Mockito.when(userRepository.getFirstByEmailOrUsername(user.getEmail(), user.getUsername()))
                .thenReturn(Optional.empty());
        User mocked = createUser();
        mocked.setId(UUID.randomUUID());
        Mockito.when(userRepository.save(user)).thenReturn(mocked);

        User created = userService.create(user);
        Assertions.assertNotNull(created);
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .getFirstByEmailOrUsername(user.getEmail(), user.getUsername());
    }

    @Test
    void createFail() {
        User user = createUser();
        Mockito.when(userRepository.getFirstByEmailOrUsername(user.getEmail(), user.getUsername()))
                .thenReturn(Optional.of(createUser()));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> userService.create(user));
        Mockito.verify(userRepository, Mockito.times(1))
                .getFirstByEmailOrUsername(user.getEmail(), user.getUsername());
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
