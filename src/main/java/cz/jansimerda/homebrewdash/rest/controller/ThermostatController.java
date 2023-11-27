package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.Thermostat;
import cz.jansimerda.homebrewdash.rest.dto.request.ThermostatRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.ThermostatResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v1/thermostats")
public class ThermostatController extends AbstractCrudController<Thermostat, ThermostatRequestDto, ThermostatResponseDto, UUID> {
    public ThermostatController(
            AbstractCrudService<Thermostat, UUID> service,
            Function<Thermostat, ThermostatResponseDto> toDtoConverter,
            Function<ThermostatRequestDto, Thermostat> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
