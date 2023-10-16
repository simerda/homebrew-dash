package cz.jansimerda.homebrewdash.authentication;

import cz.jansimerda.homebrewdash.business.UserSessionService;
import cz.jansimerda.homebrewdash.model.UserSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final UserSessionService userSessionService;

    public AuthenticationProvider(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication
    ) {
        // none required
    }

    @Override
    protected UserDetails retrieveUser(
            String username,
            UsernamePasswordAuthenticationToken authToken
    ) throws AuthenticationException {
        String token = (String) authToken.getCredentials();
        if (token == null || token.isEmpty()) {
            throw new BadCredentialsException("Authentication token missing");
        }
        UserSession session = userSessionService.readByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Authentication token is invalid"));

        if (session.isExpired()) {
            throw new BadCredentialsException("The session has expired");
        }

        return new CustomUserDetails(session.getUser());
    }
}
