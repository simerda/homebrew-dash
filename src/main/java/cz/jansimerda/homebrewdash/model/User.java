package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements DomainEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 50, nullable = false)
    private String password;

    @Column(length = 30, unique = true)
    private String username;

    @Column(length = 30)
    private String firstName;

    @Column(length = 30)
    private String surname;

    @Column(nullable = false)
    private boolean admin;

    /**
     * @return User entity identifier (UUID)
     */
    public UUID getId() {
        return Objects.requireNonNull(id);
    }

    public String getEmail() {
        return Objects.requireNonNull(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Objects.requireNonNull(password);
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password);
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Optional<String> getSurname() {
        return Optional.ofNullable(surname);
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return whether user has admin authorization
     */
    public boolean isAdmin() {
        return this.admin;
    }
}
