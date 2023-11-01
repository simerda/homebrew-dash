package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.rest.dto.response.HopChangeResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class HopChangeToDtoConverter implements Function<HopChange, HopChangeResponseDto> {

    private final HopToDtoConverter hopToDtoConverter;

    public HopChangeToDtoConverter(HopToDtoConverter hopToDtoConverter) {
        this.hopToDtoConverter = hopToDtoConverter;
    }

    @Override
    public HopChangeResponseDto apply(HopChange hopChange) {
        HopChangeResponseDto dto = new HopChangeResponseDto();
        dto.setId(hopChange.getId());
        dto.setUserId(hopChange.getUser().getId());
        dto.setHop(hopToDtoConverter.apply(hopChange.getHop()));
        dto.setAlphaAcidPercentage(hopChange.getAlphaAcidPercentage());
        dto.setBetaAcidPercentage(hopChange.getBetaAcidPercentage());
        dto.setHarvestedAt(hopChange.getHarvestedAt());
        dto.setChangeGrams(hopChange.getChangeGrams());
        dto.setCreatedAt(hopChange.getCreatedAt());

        return dto;
    }
}
