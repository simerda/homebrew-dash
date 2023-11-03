package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.rest.dto.response.BeerResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BeerToDtoConverter implements Function<Beer, BeerResponseDto> {

    private final GravityToDtoConverter gravityConverter;

    public BeerToDtoConverter(GravityToDtoConverter gravityConverter) {
        this.gravityConverter = gravityConverter;
    }

    @Override
    public BeerResponseDto apply(Beer beer) {
        BeerResponseDto dto = new BeerResponseDto();
        dto.setId(beer.getId());
        dto.setName(beer.getName());
        beer.getDescription().ifPresent(dto::setDescription);
        beer.getOriginalGravity().map(gravityConverter).ifPresent(dto::setOriginalGravity);
        beer.getAlcoholByVolume().ifPresent(dto::setAlcoholByVolume);
        beer.getBitternessIbu().ifPresent(dto::setBitternessIbu);
        beer.getColorEbc().ifPresent(dto::setColorEbc);
        beer.getVolumeBrewed().ifPresent(dto::setVolumeBrewed);
        beer.getVolumeRemaining().ifPresent(dto::setVolumeRemaining);
        beer.getFinalGravityThreshold().map(gravityConverter).ifPresent(dto::setFinalGravityThreshold);
        beer.getFinalGravity().map(gravityConverter).ifPresent(dto::setFinalGravity);
        beer.getFermentationTemperatureThreshold().ifPresent(dto::setFermentationTemperatureThreshold);
        dto.setState(beer.getState());
        beer.getBrewedAt().ifPresent(dto::setBrewedAt);
        beer.getFermentedAt().ifPresent(dto::setFermentedAt);
        beer.getMaturedAt().ifPresent(dto::setMaturedAt);
        beer.getConsumedAt().ifPresent(dto::setConsumedAt);
        dto.setCreatedByUserId(beer.getCreatedBy().getId());
        dto.setUpdatedAt(beer.getUpdatedAt());
        dto.setCreatedAt(beer.getCreatedAt());

        return dto;
    }
}
