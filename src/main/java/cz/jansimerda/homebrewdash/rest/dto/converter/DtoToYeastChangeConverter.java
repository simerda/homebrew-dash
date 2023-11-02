package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.rest.dto.request.YeastChangeRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class DtoToYeastChangeConverter implements Function<YeastChangeRequestDto, YeastChange> {

    @Override
    public YeastChange apply(YeastChangeRequestDto dto) {
        User user = new User();
        user.setId(UUID.fromString(dto.getUserId()));
        Yeast yeast = new Yeast();
        yeast.setId(UUID.fromString(dto.getYeastId()));
        YeastChange change = new YeastChange();
        change.setUser(user);
        change.setYeast(yeast);
        change.setExpirationDate(dto.getExpirationDate());
        change.setChangeGrams(dto.getChangeGrams());

        return change;
    }
}
