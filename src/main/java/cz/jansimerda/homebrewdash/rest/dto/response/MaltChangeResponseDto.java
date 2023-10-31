package cz.jansimerda.homebrewdash.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class MaltChangeResponseDto {

    private UUID id;
    private UUID userId;
    private MaltResponseDto malt;
    private Integer colorEbc;
    private Integer changeGrams;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public MaltResponseDto getMalt() {
        return malt;
    }

    public void setMalt(MaltResponseDto malt) {
        this.malt = malt;
    }

    public Integer getColorEbc() {
        return colorEbc;
    }

    public void setColorEbc(Integer colorEbc) {
        this.colorEbc = colorEbc;
    }

    public Integer getChangeGrams() {
        return changeGrams;
    }

    public void setChangeGrams(Integer changeGrams) {
        this.changeGrams = changeGrams;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
