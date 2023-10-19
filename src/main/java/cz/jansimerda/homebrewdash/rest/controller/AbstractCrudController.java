package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.DomainEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 * Common superclass for controllers supporting operations Create, Read, Update, Delete.
 *
 * @param <E>   Type of entity
 * @param <DRQ> Type of Request DTO
 * @param <DRS> Type of Response DTO
 * @param <ID>  Type of primary key of the entity
 */
public abstract class AbstractCrudController<E extends DomainEntity<ID>, DRQ, DRS, ID> {
    protected Function<E, DRS> entityToDtoConverter;
    protected Function<DRQ, E> dtoToEntityConverter;
    private AbstractCrudService<E, ID> service;

    public AbstractCrudController(
            AbstractCrudService<E, ID> service,
            Function<E, DRS> toDtoConverter,
            Function<DRQ, E> toEntityConverter
    ) {
        this.service = service;
        this.entityToDtoConverter = toDtoConverter;
        this.dtoToEntityConverter = toEntityConverter;
    }

    @PostMapping
    public ResponseEntity<DRS> create(@Valid @RequestBody DRQ request) {
        DRS responseDto = entityToDtoConverter.apply(service.create(dtoToEntityConverter.apply(request)));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<DRS>> readAll() {
        return ResponseEntity.ok(collectionToDto(service.readAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DRS> readOne(@PathVariable ID id) {
        var entity = service.readById(id);

        return entity.map(e -> ResponseEntity.ok(entityToDtoConverter.apply(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DRS> update(@Valid @RequestBody DRQ request, @PathVariable ID id) {
        E entity = dtoToEntityConverter.apply(request);
        entity.setId(id);
        try {
            entity = service.update(entity);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(entityToDtoConverter.apply(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) {
        try {
            service.deleteById(id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Utility method converting collection of entities to a list of Response DTOs.
     *
     * @param entities iterable of entities
     * @return List of Response DTO for each entity
     */
    protected List<DRS> collectionToDto(Iterable<E> entities) {
        return StreamSupport.stream(entities.spliterator(), false).map(entityToDtoConverter).toList();
    }
}
