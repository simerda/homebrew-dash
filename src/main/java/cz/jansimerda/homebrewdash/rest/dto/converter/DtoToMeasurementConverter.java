package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.model.enums.TemperatureUnitEnum;
import cz.jansimerda.homebrewdash.rest.dto.request.MeasurementCreateRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

@Component
public class DtoToMeasurementConverter implements Function<MeasurementCreateRequestDto, Measurement> {

    @Override
    public Measurement apply(MeasurementCreateRequestDto dto) {
        Measurement measurement = new Measurement();
        measurement.setAngle(dto.getAngle());

        measurement.setTemperature(getTemperatureCelsius(dto.getTemperature(), dto.getTemperatureUnit()));
        measurement.setBattery(dto.getBattery());
        measurement.setSpecificGravity(dto.getGravity());
        measurement.setInterval(dto.getInterval());
        measurement.setRssi(dto.getRssi());

        return measurement;
    }

    /**
     * Convert given temperature value and unit to Celsius
     *
     * @param value temperature value
     * @param unit  temperature unit
     * @return value in Celsius
     */
    private BigDecimal getTemperatureCelsius(BigDecimal value, TemperatureUnitEnum unit) {
        if (unit.equals(TemperatureUnitEnum.K)) {
            return value.subtract(new BigDecimal("273.15"));
        }

        if (unit.equals(TemperatureUnitEnum.F)) {
            // C = (F - 32) * 5/9
            return value.subtract(new BigDecimal(32))
                    .multiply(new BigDecimal(5))
                    .divide(new BigDecimal(9), RoundingMode.HALF_UP);
        }

        return value;
    }
}
