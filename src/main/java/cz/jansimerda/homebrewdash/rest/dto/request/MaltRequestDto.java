package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
public class MaltRequestDto {

    @Size(min = 3, max = 200)
    @NotBlank
    private String name;

    @Size(min = 1, max = 100)
    private String manufacturerName;

    public String getName() {
        return StringUtils.trim(name);
    }

    public String getManufacturerName() {
        return StringUtils.trim(manufacturerName);
    }
}
