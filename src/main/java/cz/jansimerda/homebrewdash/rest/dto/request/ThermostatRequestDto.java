package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public class ThermostatRequestDto {

    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 20)
    @NotBlank
    private String deviceName;

    @Size(max = 100)
    @Email
    @NotBlank
    private String email;

    @Size(max = 150)
    @NotBlank
    private String password;

    @NotNull
    private boolean heating;

    @NotNull
    private boolean active;

    @org.hibernate.validator.constraints.UUID
    private String hydrometerId;

    public String getName() {
        return name;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isHeating() {
        return heating;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getHydrometerId() {
        return hydrometerId == null ? null : UUID.fromString(hydrometerId);
    }
}
