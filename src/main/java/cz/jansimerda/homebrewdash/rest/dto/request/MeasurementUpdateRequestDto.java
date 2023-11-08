package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public class MeasurementUpdateRequestDto {
    @org.hibernate.validator.constraints.UUID
    private String beerId;

    @NotNull
    private boolean hidden;

    public UUID getBeerId() {
        return beerId == null ? null : UUID.fromString(beerId);
    }

    public boolean isHidden() {
        return hidden;
    }
}
