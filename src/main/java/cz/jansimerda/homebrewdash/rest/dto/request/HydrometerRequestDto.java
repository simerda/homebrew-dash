package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public class HydrometerRequestDto {

    @NotBlank
    @Size(min = 1, max = 60)
    private String name;

    @org.hibernate.validator.constraints.UUID
    private String assignedBeerId;

    @NotNull
    private boolean active;

    public String getName() {
        return StringUtils.trim(name);
    }

    public UUID getAssignedBeerId() {
        return assignedBeerId == null ? null : UUID.fromString(assignedBeerId);
    }

    public boolean isActive() {
        return active;
    }
}
