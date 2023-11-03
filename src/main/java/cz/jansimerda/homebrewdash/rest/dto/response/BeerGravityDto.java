package cz.jansimerda.homebrewdash.rest.dto.response;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BeerGravityDto {
    private BigDecimal specificGravity;
    private String plato;

    public BigDecimal getSpecificGravity() {
        return specificGravity;
    }

    public void setSpecificGravity(BigDecimal specificGravity) {
        this.specificGravity = specificGravity;
    }

    public String getPlato() {
        return plato;
    }

    public void setPlato(BigDecimal plato) {
        this.plato = new DecimalFormat("0.00").format(plato);
    }
}
