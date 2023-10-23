package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractServiceTest extends AbstractTest {

    @MockBean
    protected UserRepository userRepository;

    private User admin;

    private User user;

    @BeforeEach
    protected void setupUsers() {
        if (admin == null) {
            admin = createAdmin();
        }

        if (user == null) {
            user = createUser();
        }

        Mockito.when(userRepository.getFirstByUsername("admin")).thenReturn(Optional.of(admin));
        Mockito.when(userRepository.getFirstByUsername("user")).thenReturn(Optional.of(user));
    }

    protected User getAdmin() {
        return admin;
    }

    protected User getUser() {
        return user;
    }

    protected List<User> getUsers() {
        return Arrays.asList(getAdmin(), getUser());
    }
}
