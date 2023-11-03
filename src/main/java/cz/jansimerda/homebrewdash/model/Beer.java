package cz.jansimerda.homebrewdash.model;

import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "beers")
@EntityListeners(AuditingEntityListener.class)
public class Beer implements DomainEntity<UUID>, CreationAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 10000)
    private String description;

    @Column(precision = 6, scale = 5)
    private BigDecimal originalGravity;

    @Column(precision = 4, scale = 2)
    private BigDecimal alcoholByVolume;

    @Column
    private Integer bitternessIbu;

    @Column
    private Integer colorEbc;

    @Column(precision = 7, scale = 1)
    private BigDecimal volumeBrewed;

    @Column(precision = 7, scale = 1)
    private BigDecimal volumeRemaining;

    @Column(precision = 6, scale = 5)
    private BigDecimal finalGravityThreshold;

    @Column(precision = 6, scale = 5)
    private BigDecimal finalGravity;

    @Column(precision = 4, scale = 2)
    private BigDecimal fermentationTemperatureThreshold;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private BrewStateEnum state;

    @Column
    private LocalDate brewedAt;

    @Column
    private LocalDate fermentedAt;

    @Column
    private LocalDate maturedAt;

    @Column
    private LocalDate consumedAt;

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
     * @return name of the beer
     */
    public String getName() {
        return Objects.requireNonNull(name);
    }

    /**
     * @param name name of the beer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return beer description
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * @param description beer description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * OG (Original Gravity) getter - OG is the SG (Specific Gravity) after brewing but before fermenting.
     * SG = gravity of sample / gravity of water
     *
     * @return OG - Original Gravity
     */
    public Optional<BigDecimal> getOriginalGravity() {
        return Optional.ofNullable(originalGravity);
    }

    /**
     * OG (Original Gravity) setter - OG is the SG (Specific Gravity) after brewing but before fermenting.
     * SG = gravity of sample / gravity of water
     *
     * @param originalGravity OG - Original Gravity
     */
    public void setOriginalGravity(BigDecimal originalGravity) {
        this.originalGravity = originalGravity;
    }

    /**
     * ABV (Alcohol by Volume) getter - percentage of alcohol in the beer
     *
     * @return ABV - alcohol by volume
     */
    public Optional<BigDecimal> getAlcoholByVolume() {
        return Optional.ofNullable(alcoholByVolume);
    }

    /**
     * ABV (Alcohol by Volume) setter - percentage of alcohol in the beer
     *
     * @param alcoholByVolume ABV - alcohol by volume
     */
    public void setAlcoholByVolume(BigDecimal alcoholByVolume) {
        this.alcoholByVolume = alcoholByVolume;
    }

    /**
     * IBU (International Bitterness Unit) getter - the scale of bitterness of the beer
     *
     * @return IBU - International Bitterness Unit
     */
    public Optional<Integer> getBitternessIbu() {
        return Optional.ofNullable(bitternessIbu);
    }

    /**
     * IBU (International Bitterness Unit) setter - the scale of bitterness of the beer
     *
     * @param bitternessIbu IBU - International Bitterness Unit
     */
    public void setBitternessIbu(Integer bitternessIbu) {
        this.bitternessIbu = bitternessIbu;
    }

    /**
     * EBC (European Brewery Convention) setter - the scale of the color of the beer
     *
     * @return EBC - European Brewery Convention
     */
    public Optional<Integer> getColorEbc() {
        return Optional.ofNullable(colorEbc);
    }

    /**
     * EBC (European Brewery Convention) setter - the scale of the color of the beer
     *
     * @param colorEbc EBC - European Brewery Convention
     */
    public void setColorEbc(Integer colorEbc) {
        this.colorEbc = colorEbc;
    }

    /**
     * @return volume of beer brewed in litres
     */
    public Optional<BigDecimal> getVolumeBrewed() {
        return Optional.ofNullable(volumeBrewed);
    }

    /**
     * @param volumeBrewed volume of beer brewed in litres
     */
    public void setVolumeBrewed(BigDecimal volumeBrewed) {
        this.volumeBrewed = volumeBrewed;
    }

    /**
     * @return remaining volume of beer in litres
     */
    public Optional<BigDecimal> getVolumeRemaining() {
        return Optional.ofNullable(volumeRemaining);
    }

    /**
     * @param volumeRemaining remaining volume of beer in litres
     */
    public void setVolumeRemaining(BigDecimal volumeRemaining) {
        this.volumeRemaining = volumeRemaining;
    }

    /**
     * FG (Final Gravity) threshold setter - FG is the SG (Specific Gravity) after fermenting.
     * SG = gravity of sample / gravity of water
     * this threshold specifies when the fermentation should be stopped
     *
     * @return FG threshold
     */
    public Optional<BigDecimal> getFinalGravityThreshold() {
        return Optional.ofNullable(finalGravityThreshold);
    }

    /**
     * FG (Final Gravity) threshold setter - FG is the SG (Specific Gravity) after fermenting.
     * SG = gravity of sample / gravity of water
     * this threshold specifies when the fermentation should be stopped
     *
     * @param finalGravityThreshold FG threshold
     */
    public void setFinalGravityThreshold(BigDecimal finalGravityThreshold) {
        this.finalGravityThreshold = finalGravityThreshold;
    }

    /**
     * FG (Final Gravity) setter - FG is the SG (Specific Gravity) after fermenting.
     * SG = gravity of sample / gravity of water
     *
     * @return FG - Final Gravity
     */
    public Optional<BigDecimal> getFinalGravity() {
        return Optional.ofNullable(finalGravity);
    }

    /**
     * FG (Final Gravity) setter - FG is the SG (Specific Gravity) after fermenting.
     * SG = gravity of sample / gravity of water
     *
     * @param finalGravity FG - Final Gravity
     */
    public void setFinalGravity(BigDecimal finalGravity) {
        this.finalGravity = finalGravity;
    }

    /**
     * @return optimal fermenting temperature in degrees Celsius
     */
    public Optional<BigDecimal> getFermentationTemperatureThreshold() {
        return Optional.ofNullable(fermentationTemperatureThreshold);
    }

    /**
     * @param fermentationTemperatureThreshold optimal fermenting temperature in degrees Celsius
     */
    public void setFermentationTemperatureThreshold(BigDecimal fermentationTemperatureThreshold) {
        this.fermentationTemperatureThreshold = fermentationTemperatureThreshold;
    }

    /**
     * @return state of the beer brewing lifecycle
     */
    public BrewStateEnum getState() {
        return Objects.requireNonNull(state);
    }

    /**
     * @param state state of the beer brewing lifecycle
     */
    public void setState(BrewStateEnum state) {
        this.state = state;
    }

    /**
     * @return date of brewing (and start of fermentation)
     */
    public Optional<LocalDate> getBrewedAt() {
        return Optional.ofNullable(brewedAt);
    }

    /**
     * @param brewedAt date of brewing (and start of fermentation)
     */
    public void setBrewedAt(LocalDate brewedAt) {
        this.brewedAt = brewedAt;
    }

    /**
     * @return date of the end of fermentation (and start of maturation)
     */
    public Optional<LocalDate> getFermentedAt() {
        return Optional.ofNullable(fermentedAt);
    }

    /**
     * @param fermentedAt date of the end of fermentation (and start of maturation)
     */
    public void setFermentedAt(LocalDate fermentedAt) {
        this.fermentedAt = fermentedAt;
    }

    /**
     * @return date of the end of maturation period (the beer is ready to drink)
     */
    public Optional<LocalDate> getMaturedAt() {
        return Optional.ofNullable(maturedAt);
    }

    /**
     * @param maturedAt date of the end of maturation period (the beer is ready to drink)
     */
    public void setMaturedAt(LocalDate maturedAt) {
        this.maturedAt = maturedAt;
    }

    /**
     * @return date when the remaining volume reached zero
     */
    public Optional<LocalDate> getConsumedAt() {
        return Optional.ofNullable(consumedAt);
    }

    /**
     * @param consumedAt date of consumption
     */
    public void setConsumedAt(LocalDate consumedAt) {
        this.consumedAt = consumedAt;
    }

    /**
     * @return user who created the record
     */
    public User getCreatedBy() {
        return Objects.requireNonNull(createdBy);
    }

    /**
     * @param createdBy user who created the record
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
