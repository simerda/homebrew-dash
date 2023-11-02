package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "yeast_changes")
@EntityListeners(AuditingEntityListener.class)
public class YeastChange implements DomainEntity<UUID>, CreationAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "yeast_id", nullable = false)
    private Yeast yeast;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private LocalDate expirationDate;

    @Column(nullable = false)
    private int changeGrams;

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

    /**
     * @return yeast change is attached to
     */
    public Yeast getYeast() {
        return Objects.requireNonNull(yeast);
    }

    /**
     * @param yeast yeast change is attached to
     */
    public void setYeast(Yeast yeast) {
        this.yeast = yeast;
    }

    /**
     * @return user change is attached to
     */
    public User getUser() {
        return Objects.requireNonNull(user);
    }

    /**
     * @param user user change is attached to
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return expiration date of yeast
     */
    public Optional<LocalDate> getExpirationDate() {
        return Optional.ofNullable(expirationDate);
    }

    /**
     * @param expirationDate expiration date of yeast
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @return count of added or subtracted grams of yeast
     */
    public int getChangeGrams() {
        return changeGrams;
    }

    /**
     * @param changeGrams count of added or subtracted grams of yeast
     */
    public void setChangeGrams(int changeGrams) {
        this.changeGrams = changeGrams;
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
