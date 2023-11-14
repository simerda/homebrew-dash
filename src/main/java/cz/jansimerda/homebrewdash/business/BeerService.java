package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.enums.BrewStateEnum;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static cz.jansimerda.homebrewdash.model.enums.BrewStateEnum.*;

@Service
public class BeerService extends AbstractCrudService<Beer, UUID> {

    private final BeerRepository repository;

    protected BeerService(BeerRepository repository) {
        super(repository);
        this.repository = repository;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Beer create(Beer entity) {
        checkConstraints(entity);
        fillDates(entity);

        entity.setCreatedBy(AuthenticationHelper.getUser());
        return super.create(entity);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Optional<Beer> readById(UUID id) {
        Optional<Beer> beer = super.readById(id);
        beer.ifPresent(this::ensureIsAccessible);

        return beer;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Beer> readAll() {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();
        if (details.isAdmin()) {
            return super.readAll();
        }

        return repository.findByCreatedById(details.getId());
    }

    /**
     * @inheritDoc
     */
    @Override
    public Beer update(Beer entity) throws EntityNotFoundException {
        Beer existing = repository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));
        ensureIsAccessible(existing);

        // keep entity dates
        entity.setCreatedAt(existing.getCreatedAt());
        existing.getBrewedAt().ifPresent(entity::setBrewedAt);
        existing.getFermentedAt().ifPresent(entity::setFermentedAt);
        existing.getMaturedAt().ifPresent(entity::setMaturedAt);
        existing.getConsumedAt().ifPresent(entity::setConsumedAt);

        // keep author
        entity.setCreatedBy(existing.getCreatedBy());
        checkConstraints(entity);
        fillDates(entity);

        return repository.save(entity);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Beer beer = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Beer.class, id));
        ensureIsAccessible(beer);
        repository.deleteById(id);
    }

    /**
     * Sets brewed_at, fermented_at, matured_at, consumed_at values based on the state of given entity attributes
     *
     * @param entity beer entity
     */
    private void fillDates(Beer entity) {
        BrewStateEnum state = entity.getState();

        // brewed at -> null if state == PLANNING
        if (state.equals(PLANNING)) {
            entity.setBrewedAt(null);
        }

        // brewed at -> NOW() if brewed at == null AND state > PLANNING AND state != BOTCHED
        if (entity.getBrewedAt().isEmpty() && state.compareTo(PLANNING) > 0 && !state.equals(BOTCHED)) {
            entity.setBrewedAt(LocalDate.now());
        }

        // fermented at -> null if state <= FERMENTING
        if (state.compareTo(FERMENTING) <= 0) {
            entity.setFermentedAt(null);
        }

        // fermented at -> NOW() if fermented at == null AND state > FERMENTING AND state != BOTCHED
        if (entity.getFermentedAt().isEmpty() && state.compareTo(FERMENTING) > 0 && !state.equals(BOTCHED)) {
            entity.setFermentedAt(LocalDate.now());
        }

        // matured at -> null if state <= MATURING
        if (state.compareTo(MATURING) <= 0) {
            entity.setMaturedAt(null);
        }

        // matured at -> NOW() if matured at == null AND state == DONE
        if (entity.getMaturedAt().isEmpty() && state.equals(DONE)) {
            entity.setMaturedAt(LocalDate.now());
        }

        // consumed at -> null if volume remaining == null OR volume remaining > 0
        if (entity.getVolumeRemaining().filter(v -> v.signum() <= 0).isEmpty()) {
            entity.setConsumedAt(null);
        }

        // consumed at -> NOW() if consumed at == null AND volume remaining != null AND volume remaining == 0
        if (entity.getConsumedAt().isEmpty() && entity.getVolumeRemaining().filter(v -> v.signum() <= 0).isPresent()) {
            entity.setConsumedAt(LocalDate.now());
        }
    }

    /**
     * Ensures the Beer entity is in a consistent state
     *
     * @param entity beer entity to be checked
     * @throws ConditionsNotMetException if the state is invalid
     */
    private void checkConstraints(Beer entity) throws ConditionsNotMetException {
        // volume brewed must not be set before fermenting state
        if (entity.getVolumeBrewed().isPresent() && entity.getState().compareTo(FERMENTING) < 0) {
            throw new ConditionsNotMetException("Volume brewed must not be set before reaching FERMENTING state");
        }

        // volume remaining must not be set when volume brewed is null
        if (entity.getVolumeRemaining().isPresent() && entity.getVolumeBrewed().isEmpty()) {
            throw new ConditionsNotMetException("Cannot set volume remaining before volume brewed");
        }

        // volume remaining must not be set before or if botched
        if (entity.getVolumeRemaining().isPresent() && entity.getState().equals(BOTCHED)) {
            throw new ConditionsNotMetException("Volume remaining must not be set when the brew was botched");

        }

        // volume remaining must not be greater than volume brewed
        if (entity.getVolumeRemaining().isPresent()
                && entity.getVolumeBrewed().isPresent()
                && entity.getVolumeRemaining().get().compareTo(entity.getVolumeBrewed().get()) > 0
        ) {
            throw new ConditionsNotMetException("Volume remaining cannot exceed volume brewed");
        }
    }

    /**
     * Ensures that current user has access to given beer entity, otherwise throws error
     *
     * @param entity beer entity
     */
    private void ensureIsAccessible(Beer entity) throws AccessDeniedException {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();

        if (!details.isAdmin() && !entity.getCreatedBy().getId().equals(details.getId())) {
            throw new AccessDeniedException("You don't have access to this beer");
        }
    }
}
