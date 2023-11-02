package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.rest.validation.constraints.Date;
import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Validated
public class HopChangeRequestDto {
    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String hopId;

    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String userId;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @NotNull
    private BigDecimal alphaAcidPercentage;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @NotNull
    private BigDecimal betaAcidPercentage;

    @NotNull
    @Date(before = Date.DATE_NOW, beforeInclusive = true)
    private String harvestedAt;

    @NotZero
    @NotNull
    private Integer changeGrams;

    public UUID getHopId() {
        return UUID.fromString(hopId);
    }

    public UUID getUserId() {
        return UUID.fromString(userId);
    }

    public BigDecimal getAlphaAcidPercentage() {
        return alphaAcidPercentage;
    }

    public BigDecimal getBetaAcidPercentage() {
        return betaAcidPercentage;
    }

    public LocalDate getHarvestedAt() {
        return LocalDate.parse(harvestedAt, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }
}
