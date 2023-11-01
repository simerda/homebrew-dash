package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.rest.dto.request.HopRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.HopResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v0/hops")
public class HopController extends AbstractCrudController<Hop, HopRequestDto, HopResponseDto, UUID> {
    public HopController(
            AbstractCrudService<Hop, UUID> service,
            Function<Hop, HopResponseDto> toDtoConverter,
            Function<HopRequestDto, Hop> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
