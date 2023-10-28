package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.rest.dto.request.MaltRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToMaltConverter implements Function<MaltRequestDto, Malt> {

    @Override
    public Malt apply(MaltRequestDto dto) {
        Malt malt = new Malt();
        malt.setName(dto.getName());
        malt.setManufacturerName(dto.getManufacturerName());

        return malt;
    }
}
