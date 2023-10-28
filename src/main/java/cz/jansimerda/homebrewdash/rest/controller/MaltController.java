package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.rest.dto.request.MaltRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.MaltResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v0/malts")
public class MaltController extends AbstractCrudController<Malt, MaltRequestDto, MaltResponseDto, UUID> {
    public MaltController(
            AbstractCrudService<Malt, UUID> service,
            Function<Malt, MaltResponseDto> toDtoConverter,
            Function<MaltRequestDto, Malt> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
