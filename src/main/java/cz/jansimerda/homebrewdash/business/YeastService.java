package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.repository.YeastRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class YeastService extends AbstractCrudService<Yeast, UUID> {

    private final YeastRepository repository;

    protected YeastService(YeastRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Yeast create(Yeast entity) throws ConditionsNotMetException {
        if (repository.existsByNameAndManufacturerName(
                entity.getName(),
                entity.getManufacturerName().orElse(null)
        )) {
            throwOnDuplicate(entity.getName(), entity.getManufacturerName());
        }

        entity.setCreatedBy(AuthenticationHelper.getUser());
        return super.create(entity);
    }

    @Override
    public Yeast update(Yeast entity) throws EntityNotFoundException, AccessDeniedException {

        if (repository.existsByNameAndManufacturerNameExceptId(
                entity.getName(),
                entity.getManufacturerName().orElse(null),
                entity.getId()
        )) {
            throwOnDuplicate(entity.getName(), entity.getManufacturerName());
        }

        Yeast existing = repository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));

        ensureCanModifyEntity(existing);

        entity.setCreatedBy(existing.getCreatedBy());
        return repository.save(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Yeast existing = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Yeast.class, id));
        ensureCanModifyEntity(existing);

        if (repository.existsByIdAndChangesIsNotNull(id)) {
            throw new ConditionsNotMetException(
                    "Yeast %s has some changes attached and therefore cannot be removed".formatted(existing.getName())
            );
        }

        repository.deleteById(id);
    }

    /**
     * Throws the exception informing user about a duplicity.
     *
     * @param name             yeast name
     * @param manufacturerName yeast manufacturer name or empty
     * @throws ConditionsNotMetException the exception informing about the name clash
     */
    private void throwOnDuplicate(String name, Optional<String> manufacturerName) throws ConditionsNotMetException {
        manufacturerName.orElseThrow(() -> new ConditionsNotMetException("Yeast %s already exists".formatted(name)));

        throw new ConditionsNotMetException(
                "Yeast %s by manufacturer %s already exists".formatted(name, manufacturerName.get())
        );
    }

    /**
     * User can modify only his own Yeast entities or has to have ADMIN role
     *
     * @param entity entity to be modified
     * @throws AccessDeniedException throws access denied exception for unauthorized users
     */
    private void ensureCanModifyEntity(Yeast entity) throws AccessDeniedException {
        User loggedIn = AuthenticationHelper.getUser();
        if (!loggedIn.isAdmin() && !entity.getCreatedBy().getId().equals(loggedIn.getId())) {
            throw new AccessDeniedException("Missing authorization to modify this yeast");
        }
    }
}
