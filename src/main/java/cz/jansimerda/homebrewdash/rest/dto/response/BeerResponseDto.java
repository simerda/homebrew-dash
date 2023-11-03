package cz.jansimerda.homebrewdash.rest.dto.response;

import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class BeerResponseDto {
    private UUID id;
    private String name;
    private String description;
    private BeerGravityDto originalGravity;
    private BigDecimal alcoholByVolume;
    private Integer bitternessIbu;
    private Integer colorEbc;
    private BigDecimal volumeBrewed;
    private BigDecimal volumeRemaining;
    private BeerGravityDto finalGravityThreshold;
    private BeerGravityDto finalGravity;
    private BigDecimal fermentationTemperatureThreshold;
    private BrewStateEnum state;
    private LocalDate brewedAt;
    private LocalDate fermentedAt;
    private LocalDate maturedAt;
    private LocalDate consumedAt;
    private UUID createdByUserId;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BeerGravityDto getOriginalGravity() {
        return originalGravity;
    }

    public void setOriginalGravity(BeerGravityDto originalGravity) {
        this.originalGravity = originalGravity;
    }

    public BigDecimal getAlcoholByVolume() {
        return alcoholByVolume;
    }

    public void setAlcoholByVolume(BigDecimal alcoholByVolume) {
        this.alcoholByVolume = alcoholByVolume;
    }

    public Integer getBitternessIbu() {
        return bitternessIbu;
    }

    public void setBitternessIbu(Integer bitternessIbu) {
        this.bitternessIbu = bitternessIbu;
    }

    public Integer getColorEbc() {
        return colorEbc;
    }

    public void setColorEbc(Integer colorEbc) {
        this.colorEbc = colorEbc;
    }

    public BigDecimal getVolumeBrewed() {
        return volumeBrewed;
    }

    public void setVolumeBrewed(BigDecimal volumeBrewed) {
        this.volumeBrewed = volumeBrewed;
    }

    public BigDecimal getVolumeRemaining() {
        return volumeRemaining;
    }

    public void setVolumeRemaining(BigDecimal volumeRemaining) {
        this.volumeRemaining = volumeRemaining;
    }

    public BeerGravityDto getFinalGravityThreshold() {
        return finalGravityThreshold;
    }

    public void setFinalGravityThreshold(BeerGravityDto finalGravityThreshold) {
        this.finalGravityThreshold = finalGravityThreshold;
    }

    public BeerGravityDto getFinalGravity() {
        return finalGravity;
    }

    public void setFinalGravity(BeerGravityDto finalGravity) {
        this.finalGravity = finalGravity;
    }

    public BigDecimal getFermentationTemperatureThreshold() {
        return fermentationTemperatureThreshold;
    }

    public void setFermentationTemperatureThreshold(BigDecimal fermentationTemperatureThreshold) {
        this.fermentationTemperatureThreshold = fermentationTemperatureThreshold;
    }

    public BrewStateEnum getState() {
        return state;
    }

    public void setState(BrewStateEnum state) {
        this.state = state;
    }

    public LocalDate getBrewedAt() {
        return brewedAt;
    }

    public void setBrewedAt(LocalDate brewedAt) {
        this.brewedAt = brewedAt;
    }

    public LocalDate getFermentedAt() {
        return fermentedAt;
    }

    public void setFermentedAt(LocalDate fermentedAt) {
        this.fermentedAt = fermentedAt;
    }

    public LocalDate getMaturedAt() {
        return maturedAt;
    }

    public void setMaturedAt(LocalDate maturedAt) {
        this.maturedAt = maturedAt;
    }

    public LocalDate getConsumedAt() {
        return consumedAt;
    }

    public void setConsumedAt(LocalDate consumedAt) {
        this.consumedAt = consumedAt;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
