package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "hop_changes")
@EntityListeners(AuditingEntityListener.class)
public class HopChange implements DomainEntity<UUID>, CreationAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "hop_id", nullable = false)
    private Hop hop;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal alphaAcidPercentage;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal betaAcidPercentage;

    @Column(nullable = false)
    private LocalDate harvestedAt;

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
     * @return hop, change is attached to
     */
    public Hop getHop() {
        return Objects.requireNonNull(hop);
    }

    /**
     * @param hop hop, change is attached to
     */
    public void setHop(Hop hop) {
        this.hop = hop;
    }

    /**
     * @return user, change is attached to
     */
    public User getUser() {
        return Objects.requireNonNull(user);
    }

    /**
     * @param user user, change is attached to
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return alpha acid percentage of the hop
     */
    public BigDecimal getAlphaAcidPercentage() {
        return Objects.requireNonNull(alphaAcidPercentage);
    }

    /**
     * @param alphaAcidPercentage alpha acid percentage of the hop
     */
    public void setAlphaAcidPercentage(BigDecimal alphaAcidPercentage) {
        this.alphaAcidPercentage = alphaAcidPercentage;
    }

    /**
     * @return beta acid percentage of the hop
     */
    public BigDecimal getBetaAcidPercentage() {
        return Objects.requireNonNull(betaAcidPercentage);
    }

    /**
     * @param betaAcidPercentage beta acid percentage of the hop
     */
    public void setBetaAcidPercentage(BigDecimal betaAcidPercentage) {
        this.betaAcidPercentage = betaAcidPercentage;
    }

    /**
     * @return date of harvest
     */
    public LocalDate getHarvestedAt() {
        return Objects.requireNonNull(harvestedAt);
    }

    /**
     * @param harvestedAt date of harvest
     */
    public void setHarvestedAt(LocalDate harvestedAt) {
        this.harvestedAt = harvestedAt;
    }

    /**
     * @return count of added or subtracted grams of the hop
     */
    public int getChangeGrams() {
        return changeGrams;
    }

    /**
     * @param changeGrams count of added or subtracted grams of the hop
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
