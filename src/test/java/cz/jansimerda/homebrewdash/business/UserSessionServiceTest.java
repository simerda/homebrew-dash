package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.UserUnauthenticatedException;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.repository.UserSessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class UserSessionServiceTest extends AbstractServiceTest {

    @Autowired
    UserSessionService sessionService;

    @MockBean
    UserSessionRepository sessionRepository;

    @Test
    void create() {
        User user = createDummyUser();
        String password = user.getPassword();
        // encode pass
        user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));

        // generate ID when saving session
        Mockito.when(sessionRepository.save(Mockito.any(UserSession.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    UserSession session = (UserSession) args[0];
                    session.setId(UUID.randomUUID());
                    return session;
                });

        Mockito.when(userRepository.getFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserSession created = sessionService.create(user.getEmail(), password);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getUser());
        Assertions.assertInstanceOf(UUID.class, created.getId());
        Assertions.assertEquals(user.getId(), created.getUser().getId());
        Assertions.assertFalse(created.isExpired());
        Assertions.assertNotNull(created.getToken());
        Assertions.assertEquals(UserSession.TOKEN_LENGTH, created.getToken().length());


        Mockito.verify(userRepository, Mockito.times(1)).getFirstByEmail(user.getEmail());
        Mockito.verify(sessionRepository, Mockito.times(1)).save(created);
    }

    @Test
    void createWrongPasswordFail() {
        User user = createDummyUser();
        String password = user.getPassword();
        // encode pass
        user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));

        // generate ID when saving session
        Mockito.when(sessionRepository.save(Mockito.any(UserSession.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    UserSession session = (UserSession) args[0];
                    session.setId(UUID.randomUUID());
                    return session;
                });

        Mockito.when(userRepository.getFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Assertions.assertThrowsExactly(
                UserUnauthenticatedException.class,
                () -> sessionService.create(user.getEmail(), "incorrectPassword00+")
        );

        Mockito.verify(userRepository, Mockito.times(1)).getFirstByEmail(user.getEmail());
    }

    @Test
    void createMailNotFoundFail() {
        User user = createDummyUser();
        String password = user.getPassword();
        // encode pass
        user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));

        // generate ID when saving session
        Mockito.when(sessionRepository.save(Mockito.any(UserSession.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    UserSession session = (UserSession) args[0];
                    session.setId(UUID.randomUUID());
                    return session;
                });

        Mockito.when(userRepository.getFirstByEmail("other@mail.com")).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(
                UserUnauthenticatedException.class,
                () -> sessionService.create("other@mail.com", password)
        );

        Mockito.verify(userRepository, Mockito.times(1)).getFirstByEmail("other@mail.com");
    }

    @Test
    void readByToken() {
        UserSession session = new UserSession();
        session.setToken("token");
        Mockito.when(sessionRepository.getFirstByToken("token")).thenReturn(Optional.of(session));
        Optional<UserSession> found = sessionService.readByToken("token");

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(session, found.get());

        Mockito.verify(sessionRepository, Mockito.times(1)).getFirstByToken("token");
    }

    @Test
    void readOne() {
        UserSession session = new UserSession();
        UUID id = UUID.randomUUID();
        session.setId(id);
        Mockito.when(sessionRepository.getFirstById(id, true)).thenReturn(Optional.of(session));
        Optional<UserSession> found = sessionService.readOne(id, true);

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(session, found.get());

        Mockito.verify(sessionRepository, Mockito.times(1)).getFirstById(id, true);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readOneAccessibleUser() {
        UserSession session = new UserSession();
        session.setId(UUID.randomUUID());
        Mockito.when(sessionRepository.getFirstByIdForUser(session.getId(), getUser().getId(), false))
                .thenReturn(Optional.of(session));

        Optional<UserSession> found = sessionService.readOneAccessible(session.getId());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(session, found.get());

        Mockito.verify(sessionRepository, Mockito.times(1))
                .getFirstByIdForUser(session.getId(), getUser().getId(), false);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readOneAccessibleAdmin() {
        UserSession session = new UserSession();
        session.setId(UUID.randomUUID());
        Mockito.when(sessionRepository.getFirstById(session.getId(), true)).thenReturn(Optional.of(session));

        Optional<UserSession> found = sessionService.readOneAccessible(session.getId());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(session, found.get());

        Mockito.verify(sessionRepository, Mockito.times(1))
                .getFirstById(session.getId(), true);
    }

    @Test
    void readAll() {
        UserSession session = new UserSession();
        session.setId(UUID.randomUUID());
        List<UserSession> sessions = List.of(session);

        Mockito.when(sessionRepository.findAll(true)).thenReturn(sessions);
        var found = sessionService.readAll(true);

        Assertions.assertEquals(sessions, found);

        Mockito.verify(sessionRepository, Mockito.times(1)).findAll(true);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAccessibleUser() {
        UserSession session = new UserSession();
        session.setId(UUID.randomUUID());
        List<UserSession> sessions = List.of(session);

        Mockito.when(sessionRepository.findByUserId(getUser().getId(), false)).thenReturn(sessions);
        var found = sessionService.readAllAccessible();

        Assertions.assertEquals(sessions, found);

        Mockito.verify(sessionRepository, Mockito.times(1)).findByUserId(getUser().getId(), false);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void readAllAccessibleAdmin() {
        UserSession session = new UserSession();
        session.setId(UUID.randomUUID());
        List<UserSession> sessions = List.of(session);

        Mockito.when(sessionRepository.findAll(true)).thenReturn(sessions);
        var found = sessionService.readAllAccessible();

        Assertions.assertEquals(sessions, found);

        Mockito.verify(sessionRepository, Mockito.times(1)).findAll(true);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void expireByIdUser() {
        UserSession session = new UserSession();
        UUID id = UUID.randomUUID();
        session.setId(id);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        Assertions.assertFalse(session.isExpired());

        Mockito.when(sessionRepository.getFirstByIdForUser(id, getUser().getId(), false))
                .thenReturn(Optional.of(session));
        sessionService.expireById(id);

        Assertions.assertTrue(session.isExpired());

        Mockito.verify(sessionRepository, Mockito.times(1))
                .getFirstByIdForUser(id, getUser().getId(), false);
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void expireByIdAdmin() {
        UserSession session = new UserSession();
        UUID id = UUID.randomUUID();
        session.setId(id);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        Assertions.assertFalse(session.isExpired());

        Mockito.when(sessionRepository.getFirstById(id, true))
                .thenReturn(Optional.of(session));
        sessionService.expireById(id);

        Assertions.assertTrue(session.isExpired());

        Mockito.verify(sessionRepository, Mockito.times(1)).getFirstById(id, true);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void expireExpired() {
        UserSession session = new UserSession();
        UUID id = UUID.randomUUID();
        LocalDateTime expiredDate = LocalDateTime.now().minusMinutes(5);
        session.setId(id);
        session.setExpiresAt(expiredDate);
        Assertions.assertTrue(session.isExpired());

        Mockito.when(sessionRepository.getFirstByIdForUser(id, getUser().getId(), false))
                .thenReturn(Optional.of(session));
        sessionService.expireById(id);

        Assertions.assertEquals(expiredDate, session.getExpiresAt());

        Mockito.verify(sessionRepository, Mockito.times(1))
                .getFirstByIdForUser(id, getUser().getId(), false);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void expireUnauthorizedFail() {
        UserSession session = new UserSession();
        UUID id = UUID.randomUUID();
        LocalDateTime expiredDate = LocalDateTime.now().minusMinutes(5);
        session.setId(id);
        session.setExpiresAt(expiredDate);
        Assertions.assertTrue(session.isExpired());

        Mockito.when(sessionRepository.getFirstByIdForUser(id, getUser().getId(), false))
                .thenReturn(Optional.empty());
        Assertions.assertThrowsExactly(AccessDeniedException.class, () -> sessionService.expireById(id));

        Mockito.verify(sessionRepository, Mockito.times(1))
                .getFirstByIdForUser(id, getUser().getId(), false);
    }
}
