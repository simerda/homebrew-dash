package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.rest.dto.request.ThermostatRequestDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DtoToThermostatConverter implements Function<ThermostatRequestDto, Thermostat> {
    @Override
    public Thermostat apply(ThermostatRequestDto dto) {
        Thermostat thermostat = new Thermostat();
        thermostat.setName(dto.getName());
        thermostat.setDeviceName(dto.getDeviceName());
        thermostat.setEmail(dto.getEmail());
        thermostat.setPassword(dto.getPassword());
        thermostat.setIsHeating(dto.isHeating());
        thermostat.setIsActive(dto.isActive());

        if (dto.getHydrometerId() != null) {
            Hydrometer hydrometer = new Hydrometer();
            hydrometer.setId(dto.getHydrometerId());
            thermostat.setHydrometer(hydrometer);
        }

        return thermostat;
    }
}
