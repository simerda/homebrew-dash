package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.HopChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HopChangeRepository extends JpaRepository<HopChange, UUID> {

    @Query("SELECT coalesce(sum(c.changeGrams), 0) FROM HopChange c WHERE c.hop.id = :hopId AND "
            + "c.alphaAcidPercentage = :alphaAcidPercentage AND c.betaAcidPercentage = :betaAcidPercentage AND "
            + "c.harvestedAt = :harvestedAt AND c.user.id = :userId")
    int sumChangeByHopAndUser(
            UUID hopId,
            BigDecimal alphaAcidPercentage,
            BigDecimal betaAcidPercentage,
            LocalDate harvestedAt,
            UUID userId
    );

    @Query("SELECT coalesce(sum(c.changeGrams), 0) FROM HopChange c WHERE c.hop.id = :hopId AND "
            + "c.alphaAcidPercentage = :alphaAcidPercentage AND c.betaAcidPercentage = :betaAcidPercentage AND "
            + "c.harvestedAt = :harvestedAt AND c.user.id = :userId AND c.id != :changeId")
    int sumChangeByHopAndUserExceptChangeId(
            UUID hopId,
            BigDecimal alphaAcidPercentage,
            BigDecimal betaAcidPercentage,
            LocalDate harvestedAt,
            UUID userId,
            UUID changeId
    );

    List<HopChange> findAllByUserId(UUID id);
}
