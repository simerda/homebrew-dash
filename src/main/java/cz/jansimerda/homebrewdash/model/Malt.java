package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "malts")
public class Malt implements DomainEntity<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 100)
    private String manufacturerName;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "malt")
    private List<MaltChange> changes;

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
     * @return name of the malt
     */
    public String getName() {
        return Objects.requireNonNull(name);
    }

    /**
     * @param name name of the malt
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name of the malt manufacturer
     */
    public Optional<String> getManufacturerName() {
        return Optional.ofNullable(manufacturerName);
    }

    /**
     * @param manufacturerName name of the malt manufacturer
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * @return user who created the record
     */
    public User getCreatedBy() {
        return Objects.requireNonNull(createdBy);
    }

    /**
     * @param createdBy user who created the record
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get related MaltChanges
     *
     * @return list of MaltChange
     */
    public List<MaltChange> getChanges() {
        return Objects.requireNonNull(changes);
    }
}
