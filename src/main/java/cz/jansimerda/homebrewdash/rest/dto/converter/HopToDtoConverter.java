package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.rest.dto.response.HopResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class HopToDtoConverter implements Function<Hop, HopResponseDto> {

    @Override
    public HopResponseDto apply(Hop hop) {
        HopResponseDto dto = new HopResponseDto();
        dto.setId(hop.getId());
        dto.setName(hop.getName());
        hop.getAlphaAcidPercentage().ifPresent(dto::setAlphaAcidPercentage);
        hop.getBetaAcidPercentage().ifPresent(dto::setBetaAcidPercentage);
        dto.setHopStorageIndex(hop.getHopStorageIndex());
        dto.setCreatedByUserId(hop.getCreatedBy().getId());

        return dto;
    }
}
