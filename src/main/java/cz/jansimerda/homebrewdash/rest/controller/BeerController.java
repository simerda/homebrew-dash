package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.rest.dto.request.BeerRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.BeerResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v0/beers")
public class BeerController extends AbstractCrudController<Beer, BeerRequestDto, BeerResponseDto, UUID> {
    public BeerController(
            AbstractCrudService<Beer, UUID> service,
            Function<Beer, BeerResponseDto> toDtoConverter,
            Function<BeerRequestDto, Beer> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
