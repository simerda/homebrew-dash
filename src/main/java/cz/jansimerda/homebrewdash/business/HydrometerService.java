package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.helpers.TokenHelper;
import cz.jansimerda.homebrewdash.model.Beer;
import cz.jansimerda.homebrewdash.model.Hydrometer;
import cz.jansimerda.homebrewdash.repository.BeerRepository;
import cz.jansimerda.homebrewdash.repository.HydrometerRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HydrometerService extends AbstractCrudService<Hydrometer, UUID> {

    private final HydrometerRepository hydrometerRepository;

    private final BeerRepository beerRepository;

    protected HydrometerService(HydrometerRepository hydrometerRepository, BeerRepository beerRepository) {
        super(hydrometerRepository);
        this.hydrometerRepository = hydrometerRepository;
        this.beerRepository = beerRepository;
    }

    @Override
    public Hydrometer create(Hydrometer entity) {
        entity.setToken(TokenHelper.generateToken(Hydrometer.TOKEN_LENGTH));
        entity.setCreatedBy(AuthenticationHelper.getUser());
        fillAssignedBeer(entity);
        return super.create(entity);
    }

    @Override
    public Optional<Hydrometer> readById(UUID id) {
        Optional<Hydrometer> hydrometer = super.readById(id);
        hydrometer.ifPresent(this::ensureIsAccessible);

        return hydrometer;
    }

    @Override
    public List<Hydrometer> readAll() {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();
        if (details.isAdmin()) {
            return super.readAll();
        }

        return hydrometerRepository.findByCreatedById(details.getId());
    }

    @Override
    public Hydrometer update(Hydrometer entity) throws EntityNotFoundException {
        Hydrometer existing = hydrometerRepository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(entity.getClass(), entity.getId()));
        ensureIsAccessible(existing);

        // keep created at date, token and author
        entity.setCreatedAt(existing.getCreatedAt());
        entity.setCreatedBy(existing.getCreatedBy());
        entity.setToken(existing.getToken());
        fillAssignedBeer(entity);

        return hydrometerRepository.save(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Hydrometer hydrometer = hydrometerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Hydrometer.class, id));
        ensureIsAccessible(hydrometer);
        hydrometerRepository.deleteById(id);
    }

    /**
     * Ensures that current user has access to given hydrometer entity, otherwise throws error
     *
     * @param entity hydrometer entity
     */
    private void ensureIsAccessible(Hydrometer entity) throws AccessDeniedException {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();

        if (!details.isAdmin() && !entity.getCreatedBy().getId().equals(details.getId())) {
            throw new AccessDeniedException("You don't have access to this hydrometer");
        }
    }

    /**
     * Hydrates related assigned Beer entity or fails with EntityNotFoundException
     *
     * @param hydrometer hydrometer entity
     */
    private void fillAssignedBeer(Hydrometer hydrometer) throws EntityNotFoundException {
        if (hydrometer.getAssignedBeer().isEmpty()) {
            return;
        }

        UUID id = hydrometer.getAssignedBeer().get().getId();
        Beer beer = beerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Cannot find Beer instance with id %s to assign to hydrometer".formatted(id)
        ));
        if (!beer.getCreatedBy().getId().equals(hydrometer.getCreatedBy().getId())) {
            throw new AccessDeniedException("You can only assign your beer to the hydrometer");
        }

        hydrometer.setAssignedBeer(beer);
    }
}
