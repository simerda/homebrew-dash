package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.rest.dto.request.YeastChangeRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.YeastChangeResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v0/yeasts/changes")
public class YeastChangeController extends AbstractCrudController<YeastChange, YeastChangeRequestDto, YeastChangeResponseDto, UUID> {
    public YeastChangeController(
            AbstractCrudService<YeastChange, UUID> service,
            Function<YeastChange, YeastChangeResponseDto> toDtoConverter,
            Function<YeastChangeRequestDto, YeastChange> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
