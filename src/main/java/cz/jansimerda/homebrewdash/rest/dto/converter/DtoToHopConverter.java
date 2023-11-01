package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.rest.dto.request.HopRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToHopConverter implements Function<HopRequestDto, Hop> {

    @Override
    public Hop apply(HopRequestDto dto) {
        Hop hop = new Hop();
        hop.setName(dto.getName());
        hop.setAlphaAcidPercentage(dto.getAlphaAcidPercentage());
        hop.setBetaAcidPercentage(dto.getBetaAcidPercentage());
        hop.setHopStorageIndex(dto.getHopStorageIndex());

        return hop;
    }
}
