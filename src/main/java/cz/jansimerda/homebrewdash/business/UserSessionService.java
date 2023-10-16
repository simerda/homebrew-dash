package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.UserUnauthenticatedException;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import cz.jansimerda.homebrewdash.repository.UserSessionRepository;
import cz.jansimerda.homebrewdash.rest.dto.request.UserSessionRequestDto;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

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
     * @param requestDto user session request DTO
     * @return user session
     */
    public UserSession create(UserSessionRequestDto requestDto) {
        var exception = new UserUnauthenticatedException(
                String.format("User with email %s doesn't exist or the password is invalid", requestDto.getEmail())
        );

        User user = userRepository.getFirstByEmail(requestDto.getEmail()).orElseThrow(() -> exception);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
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
     * Retrieve all UserSessions
     *
     * @return list of UserSessions
     */
    public Iterable<UserSession> readAll() {
        return sessionRepository.findAll();
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
