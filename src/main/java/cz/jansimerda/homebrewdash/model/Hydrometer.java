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
@Table(name = "hydrometers")
@EntityListeners(AuditingEntityListener.class)
public class Hydrometer implements DomainEntity<UUID>, CreationAware {

    /**
     * Constant for access token length
     */
    public static final int TOKEN_LENGTH = 64;

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(nullable = false, length = TOKEN_LENGTH, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "assigned_beer_id")
    private Beer assignedBeer;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

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
        return id;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return name of the hydrometer
     */
    public String getName() {
        return Objects.requireNonNull(name);
    }

    /**
     * @param name name of the hydrometer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return hydrometer auth token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token hydrometer auth token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Beer getter, this specifies for which beer the Measurements are going to be saved
     *
     * @return related beer
     */
    public Optional<Beer> getAssignedBeer() {
        return Optional.ofNullable(assignedBeer);
    }

    /**
     * Beer setter, this specifies for which beer the Measurements are going to be saved
     *
     * @param assignedBeer related beer
     */
    public void setAssignedBeer(Beer assignedBeer) {
        this.assignedBeer = assignedBeer;
    }

    /**
     * @return whether is active, masks measurements as hidden if inactive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active whether is active, masks measurements as hidden if inactive
     */
    public void setIsActive(boolean active) {
        this.active = active;
    }

    /**
     * @return owner of the Hydrometer entity record
     */
    public User getCreatedBy() {
        return Objects.requireNonNull(createdBy);
    }

    /**
     * @param createdBy owner of the Hydrometer entity record
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return date and time of last update
     */
    public LocalDateTime getUpdatedAt() {
        return Objects.requireNonNull(updatedAt);
    }

    /**
     * @param updatedAt date and time of last update
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
