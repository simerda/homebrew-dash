package cz.jansimerda.homebrewdash.rest.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class YeastChangeResponseDto {
    private UUID id;
    private UUID userId;
    private YeastResponseDto yeast;
    private LocalDate expirationDate;
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

    public YeastResponseDto getYeast() {
        return yeast;
    }

    public void setYeast(YeastResponseDto yeast) {
        this.yeast = yeast;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
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
