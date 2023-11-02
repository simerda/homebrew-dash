package cz.jansimerda.homebrewdash.rest.dto.request;

import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
public class YeastRequestDto {

    @Size(min = 3, max = 200)
    @NotBlank
    private String name;

    @Size(min = 1, max = 100)
    private String manufacturerName;

    @NotNull
    private YeastTypeEnum type;

    @NotNull
    private YeastKindEnum kind;

    public String getName() {
        return StringUtils.trim(name);
    }

    public String getManufacturerName() {
        return StringUtils.trim(manufacturerName);
    }

    public YeastTypeEnum getType() {
        return type;
    }

    public YeastKindEnum getKind() {
        return kind;
    }
}
