package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public class YeastChangeRequestDto {
    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String yeastId;

    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String userId;

    private LocalDate expirationDate;

    @NotZero
    @NotNull
    private Integer changeGrams;

    public String getYeastId() {
        return yeastId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }
}
