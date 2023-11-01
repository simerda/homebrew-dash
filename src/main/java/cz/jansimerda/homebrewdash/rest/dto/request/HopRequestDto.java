package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
public class HopRequestDto {

    @Size(min = 3, max = 200)
    @NotBlank
    private String name;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal alphaAcidPercentage;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal betaAcidPercentage;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1", inclusive = false)
    @NotNull
    private BigDecimal hopStorageIndex;

    public String getName() {
        return StringUtils.trim(name);
    }

    public BigDecimal getAlphaAcidPercentage() {
        return alphaAcidPercentage;
    }

    public BigDecimal getBetaAcidPercentage() {
        return betaAcidPercentage;
    }

    public BigDecimal getHopStorageIndex() {
        return hopStorageIndex;
    }
}
