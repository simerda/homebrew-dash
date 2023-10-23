package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements DomainEntity<UUID>, CreationAware {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 30, nullable = false, unique = true)
    private String username;

    @Column(length = 30)
    private String firstName;

    @Column(length = 30)
    private String surname;

    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * @inheritDoc
     */
    @Override
    public UUID getId() {
        return Objects.requireNonNull(id);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setId(UUID id) {
        this.id = id;
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

    public String getUsername() {
        return Objects.requireNonNull(username);
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

    /**
     * @param admin give or withdraw admin authorization
     */
    public void setIsAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * @return date and time of last update
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * @param date date and time of last update
     */
    public void setUpdatedAt(LocalDateTime date) {
        this.updatedAt = date;
    }

    /**
     * @return date and time of creation
     */
    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * @param date date and time of creation
     */
    @Override
    public void setCreatedAt(LocalDateTime date) {
        this.createdAt = date;
    }
}
