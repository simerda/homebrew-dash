package cz.jansimerda.homebrewdash.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class HydrometerResponseDto {

    private UUID id;
    private String name;
    private String token;
    private UUID assignedBeerId;
    private boolean active;
    private UUID createdById;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getAssignedBeerId() {
        return assignedBeerId;
    }

    public void setAssignedBeerId(UUID assignedBeerId) {
        this.assignedBeerId = assignedBeerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setIsActive(boolean active) {
        this.active = active;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
