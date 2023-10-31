package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.MaltChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MaltChangeRepository extends JpaRepository<MaltChange, UUID> {

    @Query("SELECT coalesce(sum(c.changeGrams), 0) FROM MaltChange c WHERE c.malt.id = :maltId AND c.user.id = :userId")
    int sumChangeByMaltAndUser(UUID maltId, UUID userId);

    @Query("SELECT coalesce(sum(c.changeGrams), 0) FROM MaltChange c WHERE c.malt.id = :maltId AND "
            + "c.user.id = :userId AND c.id != :changeId")
    int sumChangeByMaltAndUserExceptChangeId(UUID maltId, UUID userId, UUID changeId);

    List<MaltChange> findAllByUserId(UUID id);
}
