package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import cz.jansimerda.homebrewdash.repository.UserSessionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public abstract class AbstractControllerTest extends AbstractTest {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserSessionRepository sessionRepository;
    private User admin;
    private User user;

    @BeforeEach
    protected void setUp() {
        admin = userRepository.save(createAdmin());
        user = userRepository.save(createUser());
    }

    @AfterEach
    protected void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * @return user with admin role from the user repository
     */
    protected User getAdmin() {
        return admin;
    }

    /**
     * @return basic user without admin role from the user repository
     */
    protected User getUser() {
        return user;
    }

    /**
     * Creates and stores a UserSession for a given user
     * and forms authentication Bearer token for the Authorization header
     *
     * @param username username of the user to be authenticated
     * @return Bearer token for the Authorization header
     */
    protected String authenticateUser(String username) {
        String token = username + "+TESTING_TOKEN";

        UserSession session = sessionRepository.getFirstByToken(token).orElse(new UserSession());
        session.setUser(userRepository.getFirstByUsername(username).orElseThrow());
        session.setToken(token);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        return "Bearer " + sessionRepository.save(session).getToken();
    }

    /**
     * Creates and stores a UserSession for the default admin user
     * and forms authentication Bearer token for the Authorization header
     *
     * @return Bearer token for the Authorization header
     */
    protected String authenticateAdmin() {
        return authenticateUser("admin");
    }

    /**
     * Creates and stores a UserSession for the default user
     * and forms authentication Bearer token for the Authorization header
     *
     * @return Bearer token for the Authorization header
     */
    protected String authenticateUser() {
        return authenticateUser("user");
    }
}
