package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.rest.dto.response.YeastResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class YeastToDtoConverter implements Function<Yeast, YeastResponseDto> {

    @Override
    public YeastResponseDto apply(Yeast yeast) {
        YeastResponseDto dto = new YeastResponseDto();
        dto.setId(yeast.getId());
        dto.setName(yeast.getName());
        dto.setType(yeast.getType());
        dto.setKind(yeast.getKind());
        yeast.getManufacturerName().ifPresent(dto::setManufacturerName);
        dto.setCreatedByUserId(yeast.getCreatedBy().getId());

        return dto;
    }
}
