package cz.jansimerda.homebrewdash.model;

import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.JoinFormula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "thermostats")
@EntityListeners(AuditingEntityListener.class)
public class Thermostat implements DomainEntity<UUID>, CreationAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 100)
    private String name;

    @Column(length = 20, nullable = false)
    private String deviceName;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 150, nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean heating;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean poweredOn;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ThermostatStateEnum state;

    @ManyToOne
    @JoinColumn(name = "hydrometer_id")
    private Hydrometer hydrometer;

    @Column
    private LocalDateTime lastSuccessAt;

    @Column
    private LocalDateTime lastFailAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinFormula("""
            (SELECT m.id FROM thermostats t
            JOIN hydrometers h ON t.hydrometer_id = h.id
            JOIN measurements m ON h.id = m.hydrometer_id
            WHERE t.id = id
            ORDER BY m.created_at DESC LIMIT 1)
            """)
    private Measurement lastMeasurement;

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
     * @return name of the thermostat
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * @param name name of the thermostat
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name of the device to request in the meross API
     */
    public String getDeviceName() {
        return Objects.requireNonNull(deviceName);
    }

    /**
     * @param deviceName name of the device to request in the meross API
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return email used to connect to the meross smart device
     */
    public String getEmail() {
        return Objects.requireNonNull(email);
    }

    /**
     * @param email email used to connect to the meross smart device
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return password used to coonect to the meross smart device
     */
    public String getPassword() {
        return Objects.requireNonNull(password);
    }

    /**
     * @param password password used to connect to the meross smart device
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return whether heating or cooling element is connected
     */
    public boolean isHeating() {
        return heating;
    }

    /**
     * @param heating whether heating or cooling element is connected
     */
    public void setIsHeating(boolean heating) {
        this.heating = heating;
    }

    /**
     * @return whether the thermostat should perform the switching or should be idle
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active whether the thermostat is actively switching or is idle
     */
    public void setIsActive(boolean active) {
        this.active = active;
    }

    /**
     * @return whether the device is on at the moment
     */
    public boolean isPoweredOn() {
        return poweredOn;
    }

    /**
     * @param poweredOn whether the device is poweredOn at the moment
     */
    public void setIsPoweredOn(boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    /**
     * @return current state
     */
    public ThermostatStateEnum getState() {
        return Objects.requireNonNull(state);
    }

    /**
     * @param state current state
     */
    public void setState(ThermostatStateEnum state) {
        this.state = state;
    }

    /**
     * @return hydrometer, thermostat is assigned to
     */
    public Optional<Hydrometer> getHydrometer() {
        return Optional.ofNullable(hydrometer);
    }

    /**
     * @param hydrometer hydrometer, thermostat is assigned to
     */
    public void setHydrometer(Hydrometer hydrometer) {
        this.hydrometer = hydrometer;
    }

    /**
     * @return timestamp of the last successful switching
     */
    public Optional<LocalDateTime> getLastSuccessAt() {
        return Optional.ofNullable(lastSuccessAt);
    }

    /**
     * @param lastSuccessAt timestamp of the last successful switching
     */
    public void setLastSuccessAt(LocalDateTime lastSuccessAt) {
        this.lastSuccessAt = lastSuccessAt;
    }

    /**
     * @return timestamp of the last time switching failed
     */
    public Optional<LocalDateTime> getLastFailAt() {
        return Optional.ofNullable(lastFailAt);
    }

    /**
     * @param lastFailAt timestamp of the last time switching failed
     */
    public void setLastFailAt(LocalDateTime lastFailAt) {
        this.lastFailAt = lastFailAt;
    }

    /**
     * @return owner of the instance
     */
    public User getCreatedBy() {
        return Objects.requireNonNull(createdBy);
    }

    /**
     * @param createdBy owner of the instance
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return date and time of last update
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
        return createdAt;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * A getter for a last (createdAt) related Measurement related through Hydrometer
     *
     * @return last related measurement
     */
    public Optional<Measurement> getLastMeasurement() {
        return Optional.ofNullable(lastMeasurement);
    }
}
