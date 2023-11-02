package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.rest.dto.request.HopChangeRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToHopChangeConverter implements Function<HopChangeRequestDto, HopChange> {

    @Override
    public HopChange apply(HopChangeRequestDto dto) {
        User user = new User();
        user.setId(dto.getUserId());
        Hop hop = new Hop();
        hop.setId(dto.getHopId());
        HopChange change = new HopChange();
        change.setUser(user);
        change.setHop(hop);
        change.setAlphaAcidPercentage(dto.getAlphaAcidPercentage());
        change.setBetaAcidPercentage(dto.getBetaAcidPercentage());
        change.setHarvestedAt(dto.getHarvestedAt());
        change.setChangeGrams(dto.getChangeGrams());

        return change;
    }
}
