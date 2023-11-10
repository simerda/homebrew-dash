package cz.jansimerda.homebrewdash.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.jansimerda.homebrewdash.model.enums.TemperatureUnitEnum;
import cz.jansimerda.homebrewdash.rest.validation.constraints.EnumValue;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
public class MeasurementCreateRequestDto {

    @NotBlank
    private String token;

    @DecimalMin("0")
    @DecimalMax("180")
    @NotNull
    private BigDecimal angle;

    @DecimalMin("-100")
    @DecimalMax("500")
    @NotNull
    private BigDecimal temperature;

    @JsonProperty("temp_units")
    @EnumValue(enumClass = TemperatureUnitEnum.class)
    @NotNull
    private String temp_units;

    @DecimalMin("0")
    @DecimalMax("5")
    @NotNull
    private BigDecimal battery;

    @DecimalMin("-200")
    @DecimalMax("200")
    @NotNull
    private BigDecimal gravity;

    @NotNull
    private Integer interval;

    @JsonProperty("RSSI")
    @Min(-100)
    @Max(100)
    @NotNull
    private Integer RSSI;

    public String getToken() {
        return token;
    }

    public BigDecimal getAngle() {
        return angle;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public TemperatureUnitEnum getTemperatureUnit() {
        return TemperatureUnitEnum.valueOf(temp_units);
    }

    public BigDecimal getBattery() {
        return battery;
    }

    public BigDecimal getGravity() {
        return gravity;
    }

    public int getInterval() {
        return interval;
    }

    public int getRssi() {
        return RSSI;
    }
}
