package cz.jansimerda.homebrewdash.rest.dto.response;

import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public class ThermostatResponseDto {
    private UUID id;
    private String name;
    private String deviceName;
    private String email;
    private boolean heating;
    private boolean active;
    private boolean on;
    private ThermostatStateEnum state;
    private HydrometerResponseDto hydrometer;
    private LocalDateTime lastSuccessAt;
    private LocalDateTime lastFailAt;
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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHeating() {
        return heating;
    }

    public void setIsHeating(boolean heating) {
        this.heating = heating;
    }

    public boolean isActive() {
        return active;
    }

    public void setIsActive(boolean active) {
        this.active = active;
    }

    public boolean isOn() {
        return on;
    }

    public void setIsOn(boolean on) {
        this.on = on;
    }

    public ThermostatStateEnum getState() {
        return state;
    }

    public void setState(ThermostatStateEnum state) {
        this.state = state;
    }

    public HydrometerResponseDto getHydrometer() {
        return hydrometer;
    }

    public void setHydrometer(HydrometerResponseDto hydrometer) {
        this.hydrometer = hydrometer;
    }

    public LocalDateTime getLastSuccessAt() {
        return lastSuccessAt;
    }

    public void setLastSuccessAt(LocalDateTime lastSuccessAt) {
        this.lastSuccessAt = lastSuccessAt;
    }

    public LocalDateTime getLastFailAt() {
        return lastFailAt;
    }

    public void setLastFailAt(LocalDateTime lastFailAt) {
        this.lastFailAt = lastFailAt;
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
