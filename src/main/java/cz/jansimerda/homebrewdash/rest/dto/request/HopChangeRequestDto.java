package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @PastOrPresent
    private LocalDate harvestedAt;

    @NotZero
    @NotNull
    private Integer changeGrams;

    public String getHopId() {
        return hopId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAlphaAcidPercentage() {
        return alphaAcidPercentage;
    }

    public BigDecimal getBetaAcidPercentage() {
        return betaAcidPercentage;
    }

    public LocalDate getHarvestedAt() {
        return harvestedAt;
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }
}
