package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.rest.validation.constraints.EnumValue;
import jakarta.validation.constraints.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
public class BeerRequestDto {

    @Size(min = 3, max = 200)
    @NotBlank
    private String name;

    @Size(min = 3, max = 10000)
    private String description;

    @DecimalMin("1")
    @DecimalMax("1.3")
    private BigDecimal originalGravity;

    @DecimalMin("0")
    @DecimalMax("20")
    private BigDecimal alcoholByVolume;

    @PositiveOrZero
    @Max(10000)
    private Integer bitternessIbu;

    @PositiveOrZero()
    @Max(100)
    private Integer colorEbc;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax("99999.9")
    private BigDecimal volumeBrewed;

    @DecimalMin("0")
    @DecimalMax(value = "100000", inclusive = false)
    private BigDecimal volumeRemaining;

    @DecimalMin("1")
    @DecimalMax("1.3")
    private BigDecimal finalGravityThreshold;

    @DecimalMin("1")
    @DecimalMax("1.3")
    private BigDecimal finalGravity;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax("80")
    private BigDecimal fermentationTemperatureThreshold;

    @EnumValue(enumClass = BrewStateEnum.class)
    @NotNull
    private String state;

    public String getName() {
        return StringUtils.trim(name);
    }

    public String getDescription() {
        return StringUtils.trim(description);
    }

    public BigDecimal getOriginalGravity() {
        return originalGravity;
    }

    public BigDecimal getAlcoholByVolume() {
        return alcoholByVolume;
    }

    public Integer getBitternessIbu() {
        return bitternessIbu;
    }

    public Integer getColorEbc() {
        return colorEbc;
    }

    public BigDecimal getVolumeBrewed() {
        return volumeBrewed;
    }

    public BigDecimal getVolumeRemaining() {
        return volumeRemaining;
    }

    public BigDecimal getFinalGravityThreshold() {
        return finalGravityThreshold;
    }

    public BigDecimal getFinalGravity() {
        return finalGravity;
    }

    public BigDecimal getFermentationTemperatureThreshold() {
        return fermentationTemperatureThreshold;
    }

    public BrewStateEnum getState() {
        return BrewStateEnum.valueOf(state);
    }
}
