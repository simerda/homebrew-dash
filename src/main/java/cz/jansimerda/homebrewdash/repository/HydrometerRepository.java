package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Hydrometer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HydrometerRepository extends JpaRepository<Hydrometer, UUID> {

    Optional<Hydrometer> getFirstByToken(String token);

    List<Hydrometer> findByCreatedById(UUID id);
}
