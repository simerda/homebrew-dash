package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.model.CreationAware;
import cz.jansimerda.homebrewdash.model.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Common superclass for business logic of all entities supporting operations Create, Read, Update, Delete.
 *
 * @param <K> Type of (primary) key.
 * @param <E> Type of entity
 */
public abstract class AbstractCrudService<E extends DomainEntity<K>, K> {
    /**
     * Reference to data (persistence) layer.
     */
    private final JpaRepository<E, K> repository;

    protected AbstractCrudService(JpaRepository<E, K> repository) {
        this.repository = repository;
    }

    /**
     * Stores a new entity.
     *
     * @param entity entity to be stored
     * @return created entity
     */
    public E create(E entity) {
        return repository.save(entity);
    }

    /**
     * Fetches an entity.
     *
     * @param id id of entity to be fetched
     * @return fetched entity
     */
    public Optional<E> readById(K id) {
        return repository.findById(id);
    }

    /**
     * Fetches all entities from the repository
     *
     * @return list of entities
     */
    public List<E> readAll() {
        return repository.findAll();
    }

    /**
     * Attempts to replace an already stored entity.
     *
     * @param entity the new state of the entity to be updated; the instance must contain a key value
     * @return updated entity
     * @throws EntityNotFoundException if the entity cannot be found
     */
    public E update(E entity) throws EntityNotFoundException {
        E existing = repository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));

        if (entity instanceof CreationAware) {
            ((CreationAware) entity).setCreatedAt(((CreationAware) existing).getCreatedAt());
        }

        return repository.save(entity);
    }

    /**
     * Attempts to delete the entity with given ID
     *
     * @param id id of the entity to be deleted
     * @throws EntityNotFoundException if the entity cannot be found
     */
    public void deleteById(K id) throws EntityNotFoundException {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
