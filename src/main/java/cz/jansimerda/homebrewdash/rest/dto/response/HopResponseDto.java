package cz.jansimerda.homebrewdash.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class HopResponseDto {
    private UUID id;
    private String name;
    private BigDecimal alphaAcidPercentage;
    private BigDecimal betaAcidPercentage;
    private BigDecimal hopStorageIndex;
    private UUID createdByUserId;

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

    public BigDecimal getHopStorageIndex() {
        return hopStorageIndex;
    }

    public void setHopStorageIndex(BigDecimal hopStorageIndex) {
        this.hopStorageIndex = hopStorageIndex;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
