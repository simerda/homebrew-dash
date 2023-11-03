package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.rest.dto.response.BeerGravityDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

@Component
public class GravityToDtoConverter implements Function<BigDecimal, BeerGravityDto> {
    @Override
    public BeerGravityDto apply(BigDecimal specificGravity) {
        BeerGravityDto dto = new BeerGravityDto();
        dto.setSpecificGravity(specificGravity);

        // P = -460.234 + 662.649 * SG - 202.41 * SG^2
        BigDecimal plato = new BigDecimal("-460.234")
                .add(new BigDecimal("662.649").multiply(specificGravity))
                .subtract(new BigDecimal("202.41").multiply(specificGravity.pow(2)));
        dto.setPlato(plato);

        return dto;
    }
}
