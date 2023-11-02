package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.rest.dto.request.YeastRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToYeastConverter implements Function<YeastRequestDto, Yeast> {

    @Override
    public Yeast apply(YeastRequestDto dto) {
        Yeast yeast = new Yeast();
        yeast.setName(dto.getName());
        yeast.setManufacturerName(dto.getManufacturerName());
        yeast.setType(dto.getType());
        yeast.setKind(dto.getKind());

        return yeast;
    }
}
