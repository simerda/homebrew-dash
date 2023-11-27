package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.rest.dto.request.HydrometerRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.HydrometerResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v1/hydrometers")
public class HydrometerController extends AbstractCrudController<Hydrometer, HydrometerRequestDto, HydrometerResponseDto, UUID> {
    public HydrometerController(
            AbstractCrudService<Hydrometer, UUID> service,
            Function<Hydrometer, HydrometerResponseDto> toDtoConverter,
            Function<HydrometerRequestDto, Hydrometer> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
