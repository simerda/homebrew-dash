package cz.jansimerda.homebrewdash.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MeasurementResponseDto {
    private UUID id;
    private BigDecimal angle;
    private BigDecimal temperature;
    private BigDecimal battery;
    private BeerGravityDto gravity;
    private int interval;
    private int rssi;
    private UUID hydrometerId;
    private UUID beerId;
    private boolean hidden;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAngle() {
        return angle;
    }

    public void setAngle(BigDecimal angle) {
        this.angle = angle;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getBattery() {
        return battery;
    }

    public void setBattery(BigDecimal battery) {
        this.battery = battery;
    }

    public BeerGravityDto getGravity() {
        return gravity;
    }

    public void setGravity(BeerGravityDto gravity) {
        this.gravity = gravity;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public UUID getHydrometerId() {
        return hydrometerId;
    }

    public void setHydrometerId(UUID hydrometerId) {
        this.hydrometerId = hydrometerId;
    }

    public UUID getBeerId() {
        return beerId;
    }

    public void setBeerId(UUID beerId) {
        this.beerId = beerId;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setIsHidden(boolean hidden) {
        this.hidden = hidden;
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
