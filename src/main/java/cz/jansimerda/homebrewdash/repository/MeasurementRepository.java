package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    List<Measurement> findByBeerCreatedById(UUID id);
}
