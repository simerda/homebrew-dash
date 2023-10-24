package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.AbstractTest;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
class UserSessionRepositoryTest extends AbstractTest {

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private User sessionUser;

    private int tokenIncrementor = 0;

    @Test
    void getFirstByToken() {
        // init
        UserSession activeSession = createSession(false);
        UserSession expiredSession = createSession(true);

        // persist
        sessionRepository.save(activeSession);
        sessionRepository.save(expiredSession);

        // test
        Assertions.assertTrue(sessionRepository.getFirstByToken(activeSession.getToken()).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstByToken(expiredSession.getToken()).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstByToken(activeSession.getToken().toUpperCase()).isEmpty());
        Assertions.assertTrue(sessionRepository.getFirstByToken(expiredSession.getToken().toUpperCase()).isEmpty());
    }

    @Test
    void getFirstById() {
        // init
        UserSession activeSession = createSession(false);
        UserSession expiredSession = createSession(true);

        // persist
        sessionRepository.save(activeSession);
        sessionRepository.save(expiredSession);

        // test
        Assertions.assertTrue(sessionRepository.getFirstById(activeSession.getId(), true).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstById(activeSession.getId(), false).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstById(expiredSession.getId(), true).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstById(expiredSession.getId(), false).isEmpty());
    }

    @Test
    void getFirstByIdForUser() {
        // init
        UserSession activeSession = createSession(false);
        UserSession expiredSession = createSession(true);

        // persist
        sessionRepository.save(activeSession);
        sessionRepository.save(expiredSession);

        // test
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(activeSession.getId(), UUID.randomUUID(), true).isEmpty());
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(activeSession.getId(), UUID.randomUUID(), false).isEmpty());
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(expiredSession.getId(), UUID.randomUUID(), true).isEmpty());
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(expiredSession.getId(), UUID.randomUUID(), false).isEmpty());

        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(activeSession.getId(), sessionUser.getId(), true).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(activeSession.getId(), sessionUser.getId(), false).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(expiredSession.getId(), sessionUser.getId(), true).isPresent());
        Assertions.assertTrue(sessionRepository.getFirstByIdForUser(expiredSession.getId(), sessionUser.getId(), false).isEmpty());
    }

    @Test
    void findByUserId() {
        // init
        UserSession activeSession = createSession(false);
        UserSession expiredSession = createSession(true);

        User firstUser = activeSession.getUser();
        User secondUser = userRepository.save(createUser());

        UserSession activeSessionSecond = createSession(secondUser, false);
        UserSession expiredSessionSecond = createSession(secondUser, true);

        // persist
        sessionRepository.save(activeSession);
        sessionRepository.save(expiredSession);
        sessionRepository.save(activeSessionSecond);
        sessionRepository.save(expiredSessionSecond);

        // test
        Assertions.assertEquals(1, sessionRepository.findByUserId(firstUser.getId(), false).size());
        Assertions.assertEquals(activeSession.getId(), sessionRepository.findByUserId(firstUser.getId(), false).get(0).getId());
        Assertions.assertEquals(2, sessionRepository.findByUserId(firstUser.getId(), true).size());
        Assertions.assertEquals(2, sessionRepository.findByUserId(firstUser.getId(), true)
                .stream()
                .map(UserSession::getId)
                .filter(i -> i.equals(activeSession.getId()) || i.equals(expiredSession.getId())).count());

        Assertions.assertEquals(1, sessionRepository.findByUserId(secondUser.getId(), false).size());
        Assertions.assertEquals(activeSessionSecond.getId(), sessionRepository.findByUserId(secondUser.getId(), false).get(0).getId());
        Assertions.assertEquals(2, sessionRepository.findByUserId(secondUser.getId(), true).size());
        Assertions.assertEquals(2, sessionRepository.findByUserId(secondUser.getId(), true)
                .stream()
                .map(UserSession::getId)
                .filter(i -> i.equals(activeSessionSecond.getId()) || i.equals(expiredSessionSecond.getId())).count());
    }

    @Test
    void findAll() {
        // init
        UserSession activeSession = createSession(false);
        UserSession expiredSession = createSession(true);

        // persist
        sessionRepository.save(activeSession);
        sessionRepository.save(expiredSession);

        // test
        Assertions.assertEquals(1, sessionRepository.findAll(false).size());
        Assertions.assertEquals(activeSession.getId(), sessionRepository.findAll(false).get(0).getId());

        Assertions.assertEquals(2, sessionRepository.findAll(true).size());
        Assertions.assertEquals(2, sessionRepository.findAll(true).stream()
                .map(UserSession::getId)
                .filter(i -> i.equals(activeSession.getId()) || i.equals(expiredSession.getId())).count());
    }


    /**
     * Utility method for creating a dummy user session
     *
     * @param expired whether the created session should be expired or valid
     * @return a new UserSession
     */
    private UserSession createSession(boolean expired) {
        if (sessionUser == null) {
            sessionUser = userRepository.save(createDummyUser());
        }

        return createSession(sessionUser, expired);
    }

    /**
     * Utility method for creating a dummy user session
     *
     * @param user    user for which the session will be created (must be already persisted)
     * @param expired whether the created session should be expired or valid
     * @return a new UserSession
     */
    private UserSession createSession(User user, boolean expired) {
        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(String.format("%063d", tokenIncrementor++) + "a");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(expired ? -5 : 5));

        return session;
    }
}
