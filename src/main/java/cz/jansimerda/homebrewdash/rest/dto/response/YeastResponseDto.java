package cz.jansimerda.homebrewdash.rest.dto.response;

import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;

import java.util.UUID;

public class YeastResponseDto {
    private UUID id;
    private String name;
    private String manufacturerName;
    private YeastTypeEnum type;
    private YeastKindEnum kind;
    private UUID createdByUserId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public YeastTypeEnum getType() {
        return type;
    }

    public void setType(YeastTypeEnum type) {
        this.type = type;
    }

    public YeastKindEnum getKind() {
        return kind;
    }

    public void setKind(YeastKindEnum kind) {
        this.kind = kind;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
