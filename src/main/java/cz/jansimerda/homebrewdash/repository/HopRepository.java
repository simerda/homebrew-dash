package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Hop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HopRepository extends JpaRepository<Hop, UUID> {

    boolean existsByName(String name);

    @Query("SELECT (count(h) > 0) from Hop h WHERE h.name = :name AND h.id != :id ")
    boolean existsByNameExceptId(String name, UUID id);

    boolean existsByIdAndChangesIsNotNull(UUID id);
}
