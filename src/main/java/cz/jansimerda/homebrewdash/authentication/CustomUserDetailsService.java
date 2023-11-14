package cz.jansimerda.homebrewdash.authentication;

import cz.jansimerda.homebrewdash.exception.exposed.UserUnauthenticatedException;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.getFirstByUsername(username);
        user.orElseThrow(() -> new UserUnauthenticatedException(
                String.format("User with username %s doesn't exist or the password is invalid", username)
        ));

        return new CustomUserDetails(user.get());
    }
}
