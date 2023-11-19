package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.rest.dto.response.ThermostatResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ThermostatToDtoConverter implements Function<Thermostat, ThermostatResponseDto> {

    private final HydrometerToDtoConverter hydrometerToDtoConverter;

    public ThermostatToDtoConverter(HydrometerToDtoConverter hydrometerToDtoConverter) {
        this.hydrometerToDtoConverter = hydrometerToDtoConverter;
    }

    @Override
    public ThermostatResponseDto apply(Thermostat thermostat) {
        ThermostatResponseDto dto = new ThermostatResponseDto();
        dto.setId(thermostat.getId());
        thermostat.getName().ifPresent(dto::setName);
        dto.setDeviceName(thermostat.getDeviceName());
        dto.setEmail(thermostat.getEmail());
        dto.setIsHeating(thermostat.isHeating());
        dto.setIsActive(thermostat.isActive());
        dto.setIsOn(thermostat.isPoweredOn());
        dto.setState(thermostat.getState());
        thermostat.getHydrometer().map(hydrometerToDtoConverter).ifPresent(dto::setHydrometer);
        thermostat.getLastSuccessAt().ifPresent(dto::setLastSuccessAt);
        thermostat.getLastFailAt().ifPresent(dto::setLastFailAt);
        dto.setCreatedById(thermostat.getCreatedBy().getId());
        dto.setUpdatedAt(thermostat.getUpdatedAt());
        dto.setCreatedAt(thermostat.getCreatedAt());

        return dto;
    }
}
