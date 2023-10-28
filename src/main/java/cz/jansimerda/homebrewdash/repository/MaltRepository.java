package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Malt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaltRepository extends JpaRepository<Malt, UUID> {

    boolean existsByNameAndManufacturerName(String name, String manufacturerName);

    @Query("SELECT (count(m) > 0) from Malt m WHERE m.name = :name AND " +
            "m.manufacturerName = :manufacturerName AND m.id != :id")
    boolean existsByNameAndManufacturerNameExceptId(String name, String manufacturerName, UUID id);

    boolean existsByIdAndChangesIsNotNull(UUID id);
}
