package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.rest.dto.request.YeastRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.YeastResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v0/yeasts")
public class YeastController extends AbstractCrudController<Yeast, YeastRequestDto, YeastResponseDto, UUID> {
    public YeastController(
            AbstractCrudService<Yeast, UUID> service,
            Function<Yeast, YeastResponseDto> toDtoConverter,
            Function<YeastRequestDto, Yeast> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
