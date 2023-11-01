package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "hops")
public class Hop implements DomainEntity<UUID>{

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(precision = 4, scale = 2)
    private BigDecimal alphaAcidPercentage;

    @Column(precision = 4, scale = 2)
    private BigDecimal betaAcidPercentage;

    @Column(nullable = false, precision = 8, scale = 7)
    private BigDecimal hopStorageIndex;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "hop")
    private List<HopChange> changes;

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
     * @return name of the hop
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of the hop
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return alpha acid percentage of the hop
     */
    public Optional<BigDecimal> getAlphaAcidPercentage() {
        return Optional.ofNullable(alphaAcidPercentage);
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
    public Optional<BigDecimal> getBetaAcidPercentage() {
        return Optional.ofNullable(betaAcidPercentage);
    }

    /**
     * @param betaAcidPercentage beta acid percentage of the hop
     */
    public void setBetaAcidPercentage(BigDecimal betaAcidPercentage) {
        this.betaAcidPercentage = betaAcidPercentage;
    }

    /**
     * @return HSI (hop storage index) of the hop
     */
    public BigDecimal getHopStorageIndex() {
        return hopStorageIndex;
    }

    /**
     * @param hopStorageIndex HSI (hop storage index) of the hop
     */
    public void setHopStorageIndex(BigDecimal hopStorageIndex) {
        this.hopStorageIndex = hopStorageIndex;
    }

    /**
     * @return user who created the record
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy user who created the record
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get related HopChanges
     *
     * @return list of HopChange
     */
    public List<HopChange> getChanges() {
        return changes;
    }
}
