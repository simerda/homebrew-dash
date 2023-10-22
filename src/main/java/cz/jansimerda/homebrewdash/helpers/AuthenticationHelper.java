package cz.jansimerda.homebrewdash.helpers;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthenticationHelper {
    /**
     * Get user details of currently logged-in user.
     *
     * @return CustomUserDetails of authenticated user
     */
    public static CustomUserDetails getUserDetails() {
        return (CustomUserDetails) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );
    }

    /**
     * Get User entity of currently logged-in user
     *
     * @return authenticated User
     */
    public static User getUser() {
        return Objects.requireNonNull(getUserDetails().getUser());
    }
}
