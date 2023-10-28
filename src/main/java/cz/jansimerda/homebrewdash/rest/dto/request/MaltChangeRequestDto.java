package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

import java.util.OptionalInt;

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

    public String getUserId() {
        return userId;
    }

    public String getMaltId() {
        return maltId;
    }

    public OptionalInt getColorEbc() {
        return colorEbc == null ? OptionalInt.empty() : OptionalInt.of(colorEbc);
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }
}
