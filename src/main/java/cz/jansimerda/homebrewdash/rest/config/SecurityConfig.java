package cz.jansimerda.homebrewdash.rest.config;

import cz.jansimerda.homebrewdash.authentication.AuthenticationFilter;
import cz.jansimerda.homebrewdash.authentication.AuthenticationProvider;
import cz.jansimerda.homebrewdash.authentication.FailedAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(
                new AuthenticationFilter(authManager(http)),
                BasicAuthenticationFilter.class
        );
        http.authorizeHttpRequests(a -> {
            a.requestMatchers("/api/docs-ui/**").permitAll();
            a.requestMatchers("/api/swagger-ui/**").permitAll();
            a.requestMatchers("/api/docs/**").permitAll();
            a.requestMatchers(HttpMethod.POST, "/api/v0/user-sessions").permitAll();
            a.requestMatchers(HttpMethod.POST, "/api/v0/users").permitAll();
            a.requestMatchers(HttpMethod.POST, "/api/v0/measurements").permitAll();
            a.anyRequest().fullyAuthenticated();
        });
        http.authenticationManager(authManager(http));
        http.exceptionHandling(a -> a.authenticationEntryPoint(authenticationEntryPoint()));
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }

    /**
     * Provide common instance implementing AuthenticationEntryPoint
     *
     * @return AuthenticationEntryPoint
     */
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new FailedAuthenticationEntryPoint();
    }
}
