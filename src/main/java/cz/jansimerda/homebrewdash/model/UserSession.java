package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
@EntityListeners(AuditingEntityListener.class)
public class UserSession implements DomainEntity<UUID>, CreationAware {

    /**
     * Constant for access token length
     */
    public static final int TOKEN_LENGTH = 64;

    /**
     * Session validity (1 hour)
     */
    public static final int EXPIRATION_SECONDS = 3600;

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = TOKEN_LENGTH, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Override
    public UUID getId() {
        return Objects.requireNonNull(id);
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return Objects.requireNonNull(user);
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return 64 character long opaque access token authenticating user session
     */
    public String getToken() {
        return Objects.requireNonNull(token);
    }

    /**
     * @param token 64 character long opaque access token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return session expiration date and time
     */
    public LocalDateTime getExpiresAt() {
        return Objects.requireNonNull(expiresAt);
    }

    /**
     * @param expiresAt session expiration date and time
     */
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Checks whether the session is expired.
     *
     * @return true if expired, false if valid
     */
    public boolean isExpired() {
        return getExpiresAt().isBefore(LocalDateTime.now());
    }

    /**
     * @inheritDoc
     */
    @Override
    public LocalDateTime getCreatedAt() {
        return Objects.requireNonNull(createdAt);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setCreatedAt(LocalDateTime date) {
        createdAt = date;
    }
}
