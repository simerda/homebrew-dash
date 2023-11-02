package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Yeast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface YeastRepository extends JpaRepository<Yeast, UUID> {

    boolean existsByNameAndManufacturerName(String name, String manufacturerName);

    @Query("SELECT (count(y) > 0) from Yeast y WHERE y.name = :name AND y.id != :id AND "
            + "((:manufacturerName IS NULL AND y.manufacturerName IS NULL) OR :manufacturerName = y.manufacturerName)")
    boolean existsByNameAndManufacturerNameExceptId(String name, String manufacturerName, UUID id);

    boolean existsByIdAndChangesIsNotNull(UUID id);
}
