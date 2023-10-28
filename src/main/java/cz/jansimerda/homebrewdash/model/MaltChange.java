package cz.jansimerda.homebrewdash.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

@Entity
@Table(name = "malt_changes")
@EntityListeners(AuditingEntityListener.class)
public class MaltChange implements DomainEntity<UUID>, CreationAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "malt_id", nullable = false)
    private Malt malt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private Integer colorEbc;

    @Column(nullable = false)
    private int changeGrams;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * @inheritDoc
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public Malt getMalt() {
        return Objects.requireNonNull(malt);
    }

    public void setMalt(Malt malt) {
        this.malt = malt;
    }

    public User getUser() {
        return Objects.requireNonNull(user);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OptionalInt getColorEbc() {
        return colorEbc == null ? OptionalInt.empty() : OptionalInt.of(colorEbc);
    }

    public void setColorEbc(Integer colorEbc) {
        this.colorEbc = colorEbc;
    }

    public int getChangeGrams() {
        return changeGrams;
    }

    public void setChangeGrams(int changeGrams) {
        this.changeGrams = changeGrams;
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
    public void setCreatedAt(LocalDateTime date) {
        createdAt = date;
    }
}
