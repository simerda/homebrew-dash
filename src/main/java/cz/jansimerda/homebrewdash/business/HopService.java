package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.HopRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HopService extends AbstractCrudService<Hop, UUID> {

    private final HopRepository repository;

    protected HopService(HopRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Hop create(Hop entity) throws ConditionsNotMetException {

        if (repository.existsByName(entity.getName())) {
            throwOnDuplicate(entity.getName());
        }

        entity.setCreatedBy(AuthenticationHelper.getUser());
        return super.create(entity);
    }

    @Override
    public Hop update(Hop entity) throws EntityNotFoundException, AccessDeniedException {

        if(repository.existsByNameExceptId(entity.getName(), entity.getId())){
            throwOnDuplicate(entity.getName());
        }

        Hop existing = repository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));
        ensureCanModifyEntity(existing);

        entity.setCreatedBy(existing.getCreatedBy());
        return repository.save(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Hop existing = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Hop.class, id));
        ensureCanModifyEntity(existing);

        if(repository.existsByIdAndChangesIsNotNull(id)) {
            throw new ConditionsNotMetException(
                    "Hop %s has some changes attached and therefore cannot be removed".formatted(existing.getName())
            );
        }

        repository.deleteById(id);
    }

    /**
     * Thrown the exception informing user about the existence of a duplicate hop
     *
     * @param name hop name
     * @throws ConditionsNotMetException the exception informing about the name clash
     */
    private void throwOnDuplicate(String name) throws ConditionsNotMetException {
        throw new ConditionsNotMetException("The %s hop already exists".formatted(name));
    }

    /**
     * User can only modify his own Hop entities or has to have the ADMIN role
     *
     * @param entity entity to be modified
     * @throws AccessDeniedException throws access denied exception for unauthorized users
     */
    private void ensureCanModifyEntity(Hop entity) throws AccessDeniedException {
        User loggedIn = AuthenticationHelper.getUser();
        if (!loggedIn.isAdmin() && !entity.getCreatedBy().getId().equals(loggedIn.getId())) {
            throw new AccessDeniedException("Missing authorization to modify this hop");
        }
    }
}
