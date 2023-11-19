package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.model.enums.ThermostatStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ThermostatRepository extends JpaRepository<Thermostat, UUID> {

    List<Thermostat> findByCreatedById(UUID id);

    @Query("""
            SELECT t FROM Thermostat t WHERE t.hydrometer IS NOT NULL AND (
                t.lastSuccessAt IS NULL
                OR (t.state != :errorState AND t.lastSuccessAt <= :successThreshold)
                OR (t.state = :errorState AND t.lastFailAt <= :errorThreshold)
            ) AND t.hydrometer.assignedBeer.state = :fermentingState
            AND t.hydrometer.assignedBeer.fermentationTemperatureThreshold IS NOT NULL
            AND t.active = TRUE
            """)
    List<Thermostat> findToBeSwitched(
            LocalDateTime successThreshold,
            LocalDateTime errorThreshold,
            ThermostatStateEnum errorState,
            BrewStateEnum fermentingState
    );
}
