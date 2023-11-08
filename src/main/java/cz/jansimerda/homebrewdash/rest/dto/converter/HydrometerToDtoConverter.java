package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.rest.dto.response.HydrometerResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class HydrometerToDtoConverter implements Function<Hydrometer, HydrometerResponseDto> {

    @Override
    public HydrometerResponseDto apply(Hydrometer hydrometer) {
        HydrometerResponseDto dto = new HydrometerResponseDto();
        dto.setId(hydrometer.getId());
        dto.setName(hydrometer.getName());
        dto.setToken(hydrometer.getToken());
        hydrometer.getAssignedBeer().map(Beer::getId).ifPresent(dto::setAssignedBeerId);
        dto.setIsActive(hydrometer.isActive());
        dto.setCreatedById(hydrometer.getCreatedBy().getId());
        dto.setUpdatedAt(hydrometer.getUpdatedAt());
        dto.setCreatedAt(hydrometer.getCreatedAt());

        return dto;
    }
}
