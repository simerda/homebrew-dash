package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.rest.dto.request.HopChangeRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.HopChangeResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v1/hops/changes")
public class HopChangeController extends AbstractCrudController<HopChange, HopChangeRequestDto, HopChangeResponseDto, UUID> {
    public HopChangeController(
            AbstractCrudService<HopChange, UUID> service,
            Function<HopChange, HopChangeResponseDto> toDtoConverter,
            Function<HopChangeRequestDto, HopChange> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
