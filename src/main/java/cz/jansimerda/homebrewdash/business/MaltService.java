package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.MaltRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MaltService extends AbstractCrudService<Malt, UUID> {

    private final MaltRepository repository;

    protected MaltService(MaltRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Malt create(Malt entity) throws ConditionsNotMetException {

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
    public Malt update(Malt entity) throws EntityNotFoundException, AccessDeniedException {

        if (repository.existsByNameAndManufacturerNameExceptId(
                entity.getName(),
                entity.getManufacturerName().orElse(null),
                entity.getId()
        )) {
            throwOnDuplicate(entity.getName(), entity.getManufacturerName());
        }

        Malt existing = repository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));

        ensureCanModifyEntity(existing);

        entity.setCreatedBy(existing.getCreatedBy());
        return repository.save(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Malt existing = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Malt.class, id));
        ensureCanModifyEntity(existing);

        if (repository.existsByIdAndChangesIsNotNull(id)) {
            throw new ConditionsNotMetException(
                    "Malt %s has some changes attached and therefore cannot be removed".formatted(existing.getName())
            );
        }

        repository.deleteById(id);
    }

    /**
     * Throws the exception informing user about a duplicity.
     *
     * @param name             malt name
     * @param manufacturerName malt manufacturer name or empty
     * @throws ConditionsNotMetException the exception informing about the name clash
     */
    private void throwOnDuplicate(String name, Optional<String> manufacturerName) throws ConditionsNotMetException {
        manufacturerName.orElseThrow(() -> new ConditionsNotMetException("Malt %s already exists".formatted(name)));

        throw new ConditionsNotMetException(
                "Malt %s by manufacturer %s already exists".formatted(name, manufacturerName.get())
        );
    }

    /**
     * User can modify only his own Malt entities or has to have ADMIN role
     *
     * @param entity entity to be modified
     * @throws AccessDeniedException throws access denied exception for unauthorized users
     */
    private void ensureCanModifyEntity(Malt entity) throws AccessDeniedException {
        User loggedIn = AuthenticationHelper.getUser();
        if (!loggedIn.isAdmin() && !entity.getCreatedBy().getId().equals(loggedIn.getId())) {
            throw new AccessDeniedException("Missing authorization to modify this malt");
        }
    }
}
