package cz.jansimerda.homebrewdash.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Arrays;

public class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Throwable exception = Arrays.stream(authException.getSuppressed())
                .findFirst()
                .orElse(authException);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(exception.getLocalizedMessage());
        //TODO: return a proper JSON response
    }
}
