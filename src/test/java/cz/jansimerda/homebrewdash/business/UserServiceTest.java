package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class UserServiceTest extends AbstractServiceTest {

    @Autowired
    UserService userService;

    @Test
    void create() {
        User user = createDummyUser();
        Mockito.when(userRepository.getFirstByEmailOrUsername(user.getEmail(), user.getUsername()))
                .thenReturn(Optional.empty());
        User mocked = createDummyUser();
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
        User user = createDummyUser();
        Mockito.when(userRepository.getFirstByEmailOrUsername(user.getEmail(), user.getUsername()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> userService.create(user));
        Mockito.verify(userRepository, Mockito.times(1))
                .getFirstByEmailOrUsername(user.getEmail(), user.getUsername());
    }

    @Test
    void update() {
        User user = createDummyUser();
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getFirstByEmailOrUsernameExceptId(user.getEmail(), user.getUsername(), user.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User updated = userService.update(user);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(user.getId(), updated.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .getFirstByEmailOrUsernameExceptId(user.getEmail(), user.getUsername(), user.getId());
    }

    @Test
    void updateFail() {
        User user = createDummyUser();
        User otherClashing = createUser();
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(otherClashing.getId())).thenReturn(Optional.of(otherClashing));
        Mockito.when(userRepository.getFirstByEmailOrUsernameExceptId(user.getEmail(), user.getUsername(), user.getId()))
                .thenReturn(Optional.of(otherClashing));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Assertions.assertThrowsExactly(ConditionsNotMetException.class, () -> userService.update(user));
        Mockito.verify(userRepository, Mockito.times(1))
                .getFirstByEmailOrUsernameExceptId(user.getEmail(), user.getUsername(), user.getId());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAccessibleUser() {
        Mockito.when(userRepository.findById(getUser().getId())).thenReturn(Optional.of(getUser()));

        List<User> users = userService.readAllAccessible();
        Mockito.verify(userRepository, Mockito.times(1)).findById(getUser().getId());

        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(users.get(0).getId(), getUser().getId());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAccessibleAdmin() {
        Mockito.when(userRepository.findAll()).thenReturn(getUsers());

        List<User> users = userService.readAllAccessible();
        Mockito.verify(userRepository, Mockito.times(1)).findAll();

        Assertions.assertEquals(getUsers().size(), users.size());
        Assertions.assertTrue(users.stream().map(User::getId).toList().containsAll(
                getUsers().stream().map(User::getId).toList())
        );
    }
}
