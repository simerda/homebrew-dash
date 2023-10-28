package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.rest.dto.response.MaltResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MaltToDtoConverter implements Function<Malt, MaltResponseDto> {

    @Override
    public MaltResponseDto apply(Malt malt) {
        MaltResponseDto dto = new MaltResponseDto();
        dto.setId(malt.getId());
        dto.setName(malt.getName());
        malt.getManufacturerName().ifPresent(dto::setManufacturerName);
        dto.setCreatedByUserId(malt.getCreatedBy().getId());

        return dto;
    }
}
