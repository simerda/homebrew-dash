package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.rest.dto.request.BeerRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToBeerConverter implements Function<BeerRequestDto, Beer> {
    @Override
    public Beer apply(BeerRequestDto dto) {
        Beer beer = new Beer();
        beer.setName(dto.getName());
        beer.setDescription(dto.getDescription());
        beer.setOriginalGravity(dto.getOriginalGravity());
        beer.setAlcoholByVolume(dto.getAlcoholByVolume());
        beer.setBitternessIbu(dto.getBitternessIbu());
        beer.setColorEbc(dto.getColorEbc());
        beer.setVolumeBrewed(dto.getVolumeBrewed());
        beer.setVolumeRemaining(dto.getVolumeRemaining());
        beer.setFinalGravityThreshold(dto.getFinalGravityThreshold());
        beer.setFinalGravity(dto.getFinalGravity());
        beer.setFermentationTemperatureThreshold(dto.getFermentationTemperatureThreshold());
        beer.setState(dto.getState());

        return beer;
    }
}
