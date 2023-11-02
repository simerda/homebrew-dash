package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.rest.dto.response.YeastChangeResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class YeastChangeToDtoConverter implements Function<YeastChange, YeastChangeResponseDto> {

    private final YeastToDtoConverter yeastToDtoConverter;

    public YeastChangeToDtoConverter(YeastToDtoConverter yeastToDtoConverter) {
        this.yeastToDtoConverter = yeastToDtoConverter;
    }

    @Override
    public YeastChangeResponseDto apply(YeastChange yeastChange) {
        YeastChangeResponseDto dto = new YeastChangeResponseDto();
        dto.setId(yeastChange.getId());
        dto.setUserId(yeastChange.getUser().getId());
        dto.setYeast(yeastToDtoConverter.apply(yeastChange.getYeast()));
        yeastChange.getExpirationDate().ifPresent(dto::setExpirationDate);
        dto.setChangeGrams(yeastChange.getChangeGrams());
        dto.setCreatedAt(yeastChange.getCreatedAt());

        return dto;
    }
}
