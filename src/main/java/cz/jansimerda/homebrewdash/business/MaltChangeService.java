package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.Malt;
import cz.jansimerda.homebrewdash.model.MaltChange;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.MaltChangeRepository;
import cz.jansimerda.homebrewdash.repository.MaltRepository;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class MaltChangeService extends AbstractCrudService<MaltChange, UUID> {

    private final MaltChangeRepository maltChangeRepository;

    private final MaltRepository maltRepository;

    private final UserRepository userRepository;

    protected MaltChangeService(
            MaltChangeRepository maltChangeRepository,
            MaltRepository maltRepository,
            UserRepository userRepository
    ) {
        super(maltChangeRepository);
        this.maltChangeRepository = maltChangeRepository;
        this.maltRepository = maltRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MaltChange create(MaltChange entity) {
        modifyingPreChecks(entity, c -> c.getChangeGrams() >= 0 || maltChangeRepository.sumChangeByMaltIdAndUserId(
                c.getMalt().getId(), c.getUser().getId()) + c.getChangeGrams() >= 0);

        return super.create(entity);
    }

    @Override
    public Optional<MaltChange> readById(UUID id) {
        Optional<MaltChange> change = super.readById(id);
        change.ifPresent(c -> ensureUserIsAccessible(c.getUser().getId()));
        return change;
    }

    @Override
    public List<MaltChange> readAll() {
        User loggedInUser = AuthenticationHelper.getUser();
        if (loggedInUser.isAdmin()) {
            return super.readAll();
        }

        return maltChangeRepository.findAllByUserId(loggedInUser.getId());
    }

    @Override
    public MaltChange update(MaltChange entity) throws EntityNotFoundException {
        modifyingPreChecks(entity, c -> maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(
                c.getMalt().getId(), c.getUser().getId(), c.getId()
        ) + c.getChangeGrams() >= 0);

        return super.update(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        MaltChange change = maltChangeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MaltChange.class, id));
        if (change.getChangeGrams() > 0 && maltChangeRepository.sumChangeByMaltIdAndUserIdExceptId(
                change.getMalt().getId(),
                change.getUser().getId(),
                id
        ) < 0) {
            throw new ConditionsNotMetException(
                    "Cannot remove this Malt change as it would cause the Malt stock to go negative"
            );
        }

        maltChangeRepository.deleteById(id);
    }

    /**
     * Runs checks before update or creation.
     * Ensures user has access for given user and ensures the malt stock won't go negative
     *
     * @param change          MaltChange entity to be checked
     * @param stockSufficient anonymous function that receives MaltChange and returns stock amount for the Malt
     */
    private void modifyingPreChecks(MaltChange change, Function<MaltChange, Boolean> stockSufficient) throws ConditionsNotMetException, EntityNotFoundException, AccessDeniedException {
        Malt malt = maltRepository.findById(change.getMalt().getId())
                .orElseThrow(() -> new EntityNotFoundException(Malt.class, change.getMalt().getId()));
        UUID userId = change.getUser().getId();

        ensureUserIsAccessible(userId);
        change.setUser(getUserOrThrow(userId));
        change.setMalt(malt);
        // if going bellow stock
        if (!stockSufficient.apply(change)) {
            throw new ConditionsNotMetException("Insufficient malt stock");
        }
    }

    /**
     * Fetches user by ID or throws an exception if not found
     *
     * @param userId ID of the user to fetch
     * @return fetched User
     */
    private User getUserOrThrow(UUID userId) throws EntityNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

    /**
     * Ensure current user can access a User with provided ID
     *
     * @param userId ID of the user to check
     * @throws AccessDeniedException thrown if current user can't access user with given id
     */
    private void ensureUserIsAccessible(UUID userId) throws AccessDeniedException {
        User loggedInUser = AuthenticationHelper.getUser();
        if (!loggedInUser.isAdmin() && !userId.equals(loggedInUser.getId())) {
            throw new AccessDeniedException("Cannot access malt changes of user with ID %s".formatted(userId));
        }
    }
}
