package cz.jansimerda.homebrewdash.authentication;

import cz.jansimerda.homebrewdash.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.isAdmin() ? List.of(new SimpleGrantedAuthority(UserRoleEnum.ADMIN.toString())) : List.of();
    }

    /**
     * @return logged User
     */
    public User getUser() {
        return user;
    }

    /**
     * @return logged user's ID
     */
    public UUID getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return true if user has ADMIN role
     */
    public boolean isAdmin() {
        return user.isAdmin();
    }
}
