package cz.jansimerda.homebrewdash.model;

import cz.jansimerda.homebrewdash.model.enums.YeastKindEnum;
import cz.jansimerda.homebrewdash.model.enums.YeastTypeEnum;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "yeasts")
public class Yeast implements DomainEntity<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 100)
    private String manufacturerName;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private YeastTypeEnum type;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private YeastKindEnum kind;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "yeast")
    private List<YeastChange> changes;

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
     * @return name of the yeast
     */
    public String getName() {
        return Objects.requireNonNull(name);
    }

    /**
     * @param name name of the yeast
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name of the yeast manufacturer
     */
    public Optional<String> getManufacturerName() {
        return Optional.ofNullable(manufacturerName);
    }

    /**
     * @param manufacturerName name of the yeast manufacturer
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * @return type of the yeast (lager or ale)
     */
    public YeastTypeEnum getType() {
        return Objects.requireNonNull(type);
    }

    /**
     * @param type type of the yeast
     */
    public void setType(YeastTypeEnum type) {
        this.type = type;
    }

    /**
     * @return kind of the yeast (dried or liquid)
     */
    public YeastKindEnum getKind() {
        return Objects.requireNonNull(kind);
    }

    /**
     * @param kind kind of the yeast
     */
    public void setKind(YeastKindEnum kind) {
        this.kind = kind;
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
     * Get related YeastChanges
     *
     * @return list of YeastChange
     */
    public List<YeastChange> getChanges() {
        return Objects.requireNonNull(changes);
    }
}
