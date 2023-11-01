package cz.jansimerda.homebrewdash.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class HopChangeResponseDto {
    private UUID id;
    private UUID userId;
    private HopResponseDto hop;
    private BigDecimal alphaAcidPercentage;
    private BigDecimal betaAcidPercentage;
    private LocalDate harvestedAt;
    private Integer changeGrams;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public HopResponseDto getHop() {
        return hop;
    }

    public void setHop(HopResponseDto hop) {
        this.hop = hop;
    }

    public BigDecimal getAlphaAcidPercentage() {
        return alphaAcidPercentage;
    }

    public void setAlphaAcidPercentage(BigDecimal alphaAcidPercentage) {
        this.alphaAcidPercentage = alphaAcidPercentage;
    }

    public BigDecimal getBetaAcidPercentage() {
        return betaAcidPercentage;
    }

    public void setBetaAcidPercentage(BigDecimal betaAcidPercentage) {
        this.betaAcidPercentage = betaAcidPercentage;
    }

    public LocalDate getHarvestedAt() {
        return harvestedAt;
    }

    public void setHarvestedAt(LocalDate harvestedAt) {
        this.harvestedAt = harvestedAt;
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }

    public void setChangeGrams(Integer changeGrams) {
        this.changeGrams = changeGrams;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
