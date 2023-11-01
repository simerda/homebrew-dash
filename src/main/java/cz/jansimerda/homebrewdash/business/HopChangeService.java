package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Hop;
import cz.jansimerda.homebrewdash.model.HopChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.HopChangeRepository;
import cz.jansimerda.homebrewdash.repository.HopRepository;
import cz.jansimerda.homebrewdash.repository.MaltChangeRepository;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class HopChangeService extends AbstractCrudService<HopChange, UUID> {

    private final HopChangeRepository hopChangeRepository;

    private final HopRepository hopRepository;

    private final UserRepository userRepository;
    private final MaltChangeRepository maltChangeRepository;

    protected HopChangeService(
            HopChangeRepository hopChangeRepository,
            HopRepository hopRepository,
            UserRepository userRepository,
            MaltChangeRepository maltChangeRepository) {
        super(hopChangeRepository);
        this.hopChangeRepository = hopChangeRepository;
        this.hopRepository = hopRepository;
        this.userRepository = userRepository;
        this.maltChangeRepository = maltChangeRepository;
    }

    @Override
    public HopChange create(HopChange entity) {
        modifyingPreChecks(entity, c -> c.getChangeGrams() >= 0 || hopChangeRepository.sumChangeByHopAndUser(
                c.getHop().getId(),
                c.getAlphaAcidPercentage(),
                c.getBetaAcidPercentage(),
                c.getHarvestedAt(),
                c.getUser().getId()
        ) + c.getChangeGrams() >= 0);

        return super.create(entity);
    }

    @Override
    public Optional<HopChange> readById(UUID id) {
        Optional<HopChange> change = super.readById(id);
        change.ifPresent(c -> ensureUserIsAccessible(c.getUser().getId()));
        return change;
    }

    @Override
    public List<HopChange> readAll() {
        User loggedInUser = AuthenticationHelper.getUser();
        if (loggedInUser.isAdmin()) {
            return super.readAll();
        }

        return hopChangeRepository.findAllByUserId(loggedInUser.getId());
    }

    @Override
    public HopChange update(HopChange entity) throws EntityNotFoundException {
        modifyingPreChecks(entity, c -> hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                c.getHop().getId(),
                c.getAlphaAcidPercentage(),
                c.getBetaAcidPercentage(),
                c.getHarvestedAt(),
                c.getUser().getId(),
                c.getId()
        ) + c.getChangeGrams() >= 0);

        return super.update(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        HopChange change = hopChangeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HopChange.class, id));

        ensureUserIsAccessible(change.getUser().getId());
        if(change.getChangeGrams() > 0 && hopChangeRepository.sumChangeByHopAndUserExceptChangeId(
                change.getHop().getId(),
                change.getAlphaAcidPercentage(),
                change.getBetaAcidPercentage(),
                change.getHarvestedAt(),
                change.getUser().getId(),
                change.getId()
        ) < 0) {
            throw new ConditionsNotMetException(
                    "Cannot remove this Hop change as it would cause the Hop stock to go negative"
            );
        }

        maltChangeRepository.deleteById(id);
    }

    /**
     * Run checks before update or creation.
     * Ensures user has access for given user and ensures hop stock won't go negative
     *
     * @param change HopChange entity to be checked
     * @param stockSufficient anonymous function that receives HopChange and returns whether the stock will be sufficient after performing the operation
     */
    private void modifyingPreChecks(HopChange change, Function<HopChange, Boolean> stockSufficient) throws ConditionsNotMetException, EntityNotFoundException, AccessDeniedException {
        Hop hop = hopRepository.findById(change.getHop().getId())
                .orElseThrow(() -> new EntityNotFoundException(Hop.class, change.getHop().getId()));
        UUID userId = change.getUser().getId();

        ensureUserIsAccessible(userId);
        change.setUser(getUserOrThrow(userId));
        change.setHop(hop);
        // if going bellow stock
        if (!stockSufficient.apply(change)) {
            throw new ConditionsNotMetException("Insufficient hop stock");
        }
    }

    /**
     * Fetches user by ID or throws and exception if not found
     *
     * @param userId ID of the user to fetch
     * @return fetched User
     */
    private User getUserOrThrow(UUID userId) throws EntityNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

    /**
     * Ensure current user can access User with provided ID
     *
     * @param userId ID of the user to check
     * @throws AccessDeniedException thrown if current user can't access user with given id
     */
    private void ensureUserIsAccessible(UUID userId) throws AccessDeniedException {
        User loggedInUser = AuthenticationHelper.getUser();
        if (!loggedInUser.isAdmin() && !userId.equals(loggedInUser.getId())) {
            throw new AccessDeniedException("Cannot access hop changes of user with ID %s".formatted(userId));
        }
    }
}
