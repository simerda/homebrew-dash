package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BeerRepository extends JpaRepository<Beer, UUID> {

    List<Beer> findByCreatedById(UUID id);
}
