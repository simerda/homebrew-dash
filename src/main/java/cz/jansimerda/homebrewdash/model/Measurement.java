package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "measurements")
@EntityListeners(AuditingEntityListener.class)
public class Measurement implements DomainEntity<UUID>, CreationAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal angle;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal temperature;

    @Column(nullable = false, precision = 12, scale = 9)
    private BigDecimal battery;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal specificGravity;

    @Column(name = "measurement_interval", nullable = false)
    private int interval;

    @Column(nullable = false)
    private int rssi;

    @ManyToOne
    @JoinColumn(name = "hydrometer_id")
    private Hydrometer hydrometer;

    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    @Column(nullable = false)
    private boolean hidden;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * @inheritDoc
     */
    @Override
    public UUID getId() {
        return Objects.requireNonNull(id);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return hydrometer angle in degrees
     */
    public BigDecimal getAngle() {
        return Objects.requireNonNull(angle);
    }

    /**
     * @param angle hydrometer angle in degrees
     */
    public void setAngle(BigDecimal angle) {
        this.angle = angle;
    }

    /**
     * @return temperature in degrees Celsius
     */
    public BigDecimal getTemperature() {
        return Objects.requireNonNull(temperature);
    }

    /**
     * @param temperature temperature in degrees Celsius
     */
    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    /**
     * @return battery level in volts
     */
    public BigDecimal getBattery() {
        return Objects.requireNonNull(battery);
    }

    /**
     * @param battery battery level in volts
     */
    public void setBattery(BigDecimal battery) {
        this.battery = battery;
    }

    /**
     * SG (Specific Gravity) getter, SG = gravity of sample / gravity of water
     *
     * @return SG - Specific Gravity
     */
    public BigDecimal getSpecificGravity() {
        return Objects.requireNonNull(specificGravity);
    }

    /**
     * SG (Specific Gravity) getter, SG = gravity of sample / gravity of water
     *
     * @param specificGravity SG - Specific Gravity
     */
    public void setSpecificGravity(BigDecimal specificGravity) {
        this.specificGravity = specificGravity;
    }

    /**
     * @return measurement interval in seconds
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @param interval measurement interval in seconds
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * @return RSSI of Wi-Fi
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * @param rssi RSSI of Wi-Fi
     */
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    /**
     * @return related Hydrometer performing the measurement
     */
    public Optional<Hydrometer> getHydrometer() {
        return Optional.ofNullable(hydrometer);
    }

    /**
     * @param hydrometer related Hydrometer performing the measurement
     */
    public void setHydrometer(Hydrometer hydrometer) {
        this.hydrometer = hydrometer;
    }

    /**
     * @return related Beer the measurement is performed for
     */
    public Beer getBeer() {
        return Objects.requireNonNull(beer);
    }

    /**
     * @param beer related Beer the measurement is performed for
     */
    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    /**
     * @return whether this data point is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden whether to hide this data point
     */
    public void setIsHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return date and time of last update
     */
    public LocalDateTime getUpdatedAt() {
        return Objects.requireNonNull(updatedAt);
    }

    /**
     * @param updatedAt date and time of last update
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @inheritDoc
     */
    @Override
    public LocalDateTime getCreatedAt() {
        return Objects.requireNonNull(createdAt);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
