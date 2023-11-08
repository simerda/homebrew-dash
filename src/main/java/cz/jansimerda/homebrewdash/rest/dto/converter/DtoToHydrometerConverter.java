package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.rest.dto.request.HydrometerRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToHydrometerConverter implements Function<HydrometerRequestDto, Hydrometer> {
    @Override
    public Hydrometer apply(HydrometerRequestDto dto) {
        Hydrometer hydrometer = new Hydrometer();
        hydrometer.setName(dto.getName());
        if (dto.getAssignedBeerId() != null) {
            Beer beer = new Beer();
            beer.setId(dto.getAssignedBeerId());
            hydrometer.setAssignedBeer(beer);
        }
        hydrometer.setIsActive(dto.isActive());

        return hydrometer;
    }
}
