package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.rest.validation.constraints.Date;
import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Validated
public class YeastChangeRequestDto {
    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String yeastId;

    @org.hibernate.validator.constraints.UUID
    @NotBlank
    private String userId;

    @Date
    private String expirationDate;

    @NotZero
    @NotNull
    private Integer changeGrams;

    public UUID getYeastId() {
        return UUID.fromString(yeastId);
    }

    public UUID getUserId() {
        return UUID.fromString(userId);
    }

    public LocalDate getExpirationDate() {
        return expirationDate == null ? null : LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }
}
