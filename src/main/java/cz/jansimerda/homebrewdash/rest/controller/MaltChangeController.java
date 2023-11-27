package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.rest.dto.request.MaltChangeRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.MaltChangeResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v1/malts/changes")
public class MaltChangeController extends AbstractCrudController<MaltChange, MaltChangeRequestDto, MaltChangeResponseDto, UUID> {
    public MaltChangeController(AbstractCrudService<MaltChange, UUID> service, Function<MaltChange, MaltChangeResponseDto> toDtoConverter, Function<MaltChangeRequestDto, MaltChange> toEntityConverter) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
