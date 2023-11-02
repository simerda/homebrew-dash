package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.YeastChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface YeastChangeRepository extends JpaRepository<YeastChange, UUID> {

    @Query("SELECT coalesce(sum(c.changeGrams), 0) FROM YeastChange c WHERE c.yeast.id = :yeastId AND "
            + "((:expirationDate IS NULL AND c.expirationDate IS NULL) OR (c.expirationDate = :expirationDate)) AND "
            + "c.user.id = :userId")
    int sumChangeByYeastAndUser(UUID yeastId, LocalDate expirationDate, UUID userId);

    @Query("SELECT coalesce(sum(c.changeGrams), 0) FROM YeastChange c WHERE c.yeast.id = :yeastId AND "
            + "((:expirationDate IS NULL AND c.expirationDate IS NULL) OR (c.expirationDate = :expirationDate)) AND "
            + "c.user.id = :userId AND c.id != :changeId")
    int sumChangeByYeastAndUserExceptChangeId(UUID yeastId, LocalDate expirationDate, UUID userId, UUID changeId);

    List<YeastChange> findAllByUserId(UUID id);
}
