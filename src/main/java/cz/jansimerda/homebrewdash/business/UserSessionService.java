package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.exception.UserUnauthenticatedException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import cz.jansimerda.homebrewdash.repository.UserSessionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserSessionService {

    private final UserSessionRepository sessionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserSessionService(UserSessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Login user and provide a UserSession client can use for authentication
     *
     * @param email user's email
     * @param password user's password
     * @return user session
     */
    public UserSession create(String email, String password) {
        var exception = new UserUnauthenticatedException(
                String.format("User with email %s doesn't exist or the password is invalid", email)
        );

        User user = userRepository.getFirstByEmail(email).orElseThrow(() -> exception);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw exception;
        }

        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(generateToken(UserSession.TOKEN_LENGTH));
        session.setExpiresAt(LocalDateTime.now().plusSeconds(UserSession.EXPIRATION_SECONDS));
        return sessionRepository.save(session);
    }

    /**
     * Retrieve a UserSession from a given authentication token, if exists
     *
     * @param token authentication opaque bearer token
     * @return Optional of UserSession
     */
    public Optional<UserSession> readByToken(String token) {
        return sessionRepository.getFirstByToken(token);
    }

    /**
     * Retrieve user session by id
     *
     * @param id          id of the session
     * @param withExpired whether to also retrieve expired session
     * @return Optional of UserSession
     */
    public Optional<UserSession> readOne(UUID id, boolean withExpired) {
        return sessionRepository.getFirstById(id, withExpired);
    }

    /**
     * Retrieve user session accessible to logged-in user by ID
     * Admins can access any UserSession
     * non-Admins can only access their non-expired UserSessions
     *
     * @param id id of the session
     * @return Optional of UserSession
     */
    public Optional<UserSession> readOneAccessible(UUID id) {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();

        if (details.isAdmin()) {
            return readOne(id, true);
        }

        return sessionRepository.getFirstByIdForUser(id, details.getId(), false);
    }

    /**
     * Retrieve all UserSessions or all unexpired UserSessions based on withExpired param
     *
     * @param withExpired returns also expired when true
     * @return list of UserSessions
     */
    public List<UserSession> readAll(boolean withExpired) {
        return sessionRepository.findAll(withExpired);
    }

    /**
     * Retrieve all UserSessions for a logged-in user or all for admin
     *
     * @return list of UserSessions
     */
    public List<UserSession> readAllAccessible() {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();
        if (details.isAdmin()) {
            return readAll(true);
        }

        return sessionRepository.findByUserId(details.getId(), false);
    }

    /**
     * Set expiration of given UserSession to current date and time
     *
     * @param id id of UserSession to be expired
     */
    public void expireById(UUID id) {
        UserSession session = readOneAccessible(id).orElseThrow(() -> new AccessDeniedException("Access Denied"));
        if (session.isExpired()) {
            return;
        }

        session.setExpiresAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    /**
     * Generate a secure random token of provided length
     * from the following charset: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~+/
     *
     * @param length token length
     * @return generated token
     */
    private String generateToken(int length) {
        final String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~+/";
        SecureRandom randomProvider = new SecureRandom();

        return randomProvider.ints(length, 0, charset.length())
                .mapToObj(charset::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
