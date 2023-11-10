package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.rest.dto.response.MeasurementResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MeasurementToDtoConverter implements Function<Measurement, MeasurementResponseDto> {

    private final GravityToDtoConverter gravityToDtoConverter;

    public MeasurementToDtoConverter(GravityToDtoConverter gravityToDtoConverter) {
        this.gravityToDtoConverter = gravityToDtoConverter;
    }

    @Override
    public MeasurementResponseDto apply(Measurement measurement) {
        MeasurementResponseDto dto = new MeasurementResponseDto();
        dto.setId(measurement.getId());
        dto.setAngle(measurement.getAngle());
        dto.setTemperature(measurement.getTemperature());
        dto.setBattery(measurement.getBattery());
        dto.setGravity(gravityToDtoConverter.apply(measurement.getSpecificGravity()));
        dto.setInterval(measurement.getInterval());
        dto.setRssi(measurement.getRssi());
        measurement.getHydrometer().map(Hydrometer::getId).ifPresent(dto::setHydrometerId);
        dto.setBeerId(measurement.getBeer().getId());
        dto.setIsHidden(measurement.isHidden());
        dto.setUpdatedAt(measurement.getUpdatedAt());
        dto.setCreatedAt(measurement.getCreatedAt());

        return dto;
    }
}
