package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.rest.dto.request.MaltChangeRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class DtoToMaltChangeConverter implements Function<MaltChangeRequestDto, MaltChange> {
    @Override
    public MaltChange apply(MaltChangeRequestDto dto) {

        User user = new User();
        user.setId(UUID.fromString(dto.getUserId()));
        Malt malt = new Malt();
        malt.setId(UUID.fromString(dto.getMaltId()));
        MaltChange change = new MaltChange();
        change.setUser(user);
        change.setMalt(malt);
        dto.getColorEbc().ifPresent(change::setColorEbc);
        change.setChangeGrams(dto.getChangeGrams());

        return change;
    }
}
