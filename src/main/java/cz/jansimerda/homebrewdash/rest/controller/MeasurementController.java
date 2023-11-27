package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.MeasurementService;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.Measurement;
import cz.jansimerda.homebrewdash.rest.dto.converter.DtoToMeasurementConverter;
import cz.jansimerda.homebrewdash.rest.dto.converter.MeasurementToDtoConverter;
import cz.jansimerda.homebrewdash.rest.dto.request.MeasurementCreateRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.request.MeasurementUpdateRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.MeasurementResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/measurements")
public class MeasurementController {

    private final MeasurementToDtoConverter entityToDtoConverter;

    private final DtoToMeasurementConverter dtoToEntityConverter;

    private final MeasurementService service;

    public MeasurementController(
            MeasurementToDtoConverter entityToDtoConverter,
            DtoToMeasurementConverter dtoToEntityConverter,
            MeasurementService service
    ) {
        this.entityToDtoConverter = entityToDtoConverter;
        this.dtoToEntityConverter = dtoToEntityConverter;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MeasurementResponseDto> create(@Valid @RequestBody MeasurementCreateRequestDto request) {
        MeasurementResponseDto responseDto = entityToDtoConverter.apply(
                service.create(dtoToEntityConverter.apply(request), request.getToken())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<MeasurementResponseDto>> readAll() {
        return ResponseEntity.ok(service.readAll().stream().map(entityToDtoConverter).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasurementResponseDto> readOne(@PathVariable UUID id) {
        var entity = service.readById(id);

        return entity.map(e -> ResponseEntity.ok(entityToDtoConverter.apply(e)))
                .orElseThrow(() -> new EntityNotFoundException(Measurement.class, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeasurementResponseDto> update(
            @Valid @RequestBody MeasurementUpdateRequestDto request,
            @PathVariable UUID id
    ) throws EntityNotFoundException {
        Measurement entity = service.update(id, request.getBeerId(), request.isHidden());

        return ResponseEntity.ok(entityToDtoConverter.apply(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) throws EntityNotFoundException {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
