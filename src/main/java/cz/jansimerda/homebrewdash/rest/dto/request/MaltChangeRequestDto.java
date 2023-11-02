package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

import java.util.OptionalInt;
import java.util.UUID;

@Validated
public class MaltChangeRequestDto {
    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String maltId;

    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String userId;

    @PositiveOrZero()
    @Max(100)
    private Integer colorEbc;

    @NotZero
    @NotNull
    private Integer changeGrams;

    public UUID getUserId() {
        return UUID.fromString(userId);
    }

    public UUID getMaltId() {
        return UUID.fromString(maltId);
    }

    public OptionalInt getColorEbc() {
        return colorEbc == null ? OptionalInt.empty() : OptionalInt.of(colorEbc);
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }
}
