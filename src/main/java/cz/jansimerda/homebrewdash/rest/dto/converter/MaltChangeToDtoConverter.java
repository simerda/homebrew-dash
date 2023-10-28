package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.rest.dto.response.MaltChangeResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MaltChangeToDtoConverter implements Function<MaltChange, MaltChangeResponseDto> {

    private final MaltToDtoConverter maltToDtoConverter;

    public MaltChangeToDtoConverter(MaltToDtoConverter maltToDtoConverter) {
        this.maltToDtoConverter = maltToDtoConverter;
    }

    @Override
    public MaltChangeResponseDto apply(MaltChange maltChange) {
        MaltChangeResponseDto dto = new MaltChangeResponseDto();
        dto.setId(maltChange.getId());
        dto.setUserId(maltChange.getUser().getId());
        dto.setMalt(maltToDtoConverter.apply(maltChange.getMalt()));
        maltChange.getColorEbc().ifPresent(dto::setColorEbc);
        dto.setChangeGrams(maltChange.getChangeGrams());

        return dto;
    }
}
