package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.exposed.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.exposed.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.Yeast;
import cz.jansimerda.homebrewdash.model.YeastChange;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import cz.jansimerda.homebrewdash.repository.YeastChangeRepository;
import cz.jansimerda.homebrewdash.repository.YeastRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class YeastChangeService extends AbstractCrudService<YeastChange, UUID> {

    private final YeastChangeRepository yeastChangeRepository;

    private final YeastRepository yeastRepository;

    private final UserRepository userRepository;

    protected YeastChangeService(
            YeastChangeRepository yeastChangeRepository,
            YeastRepository yeastRepository,
            UserRepository userRepository
    ) {
        super(yeastChangeRepository);
        this.yeastChangeRepository = yeastChangeRepository;
        this.yeastRepository = yeastRepository;
        this.userRepository = userRepository;
    }

    @Override
    public YeastChange create(YeastChange entity) {
        modifyingPreChecks(entity, c -> c.getChangeGrams() >= 0 || yeastChangeRepository.sumChangeByYeastAndUser(
                c.getYeast().getId(),
                c.getExpirationDate().orElse(null),
                c.getUser().getId()
        ) + c.getChangeGrams() >= 0);

        return super.create(entity);
    }

    @Override
    public Optional<YeastChange> readById(UUID id) {
        Optional<YeastChange> change = super.readById(id);
        change.ifPresent(c -> ensureUserIsAccessible(c.getUser().getId()));
        return change;
    }

    @Override
    public List<YeastChange> readAll() {
        User loggedInUser = AuthenticationHelper.getUser();
        if (loggedInUser.isAdmin()) {
            return super.readAll();
        }

        return yeastChangeRepository.findAllByUserId(loggedInUser.getId());
    }

    @Override
    public YeastChange update(YeastChange entity) throws EntityNotFoundException {
        modifyingPreChecks(entity, c -> yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                c.getYeast().getId(),
                c.getExpirationDate().orElse(null),
                c.getUser().getId(),
                c.getId()
        ) + c.getChangeGrams() >= 0);

        return super.update(entity);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        YeastChange change = yeastChangeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(YeastChange.class, id));

        ensureUserIsAccessible(change.getUser().getId());
        if (change.getChangeGrams() > 0 && yeastChangeRepository.sumChangeByYeastAndUserExceptChangeId(
                change.getYeast().getId(),
                change.getExpirationDate().orElse(null),
                change.getUser().getId(),
                id
        ) < 0) {
            throw new ConditionsNotMetException(
                    "Cannot remove this Yeast change as it would cause the Yeast stock to go negative"
            );
        }

        yeastChangeRepository.deleteById(id);
    }

    /**
     * Runs checks before update or creation.
     * Ensures user has access for given user and the yeast stock won't go negative
     *
     * @param change          YeastChange entity to be checked
     * @param stockSufficient anonymous function that receives YeastChange and returns whether the stock will be sufficient after performing the operation
     */
    private void modifyingPreChecks(YeastChange change, Function<YeastChange, Boolean> stockSufficient) throws ConditionsNotMetException, EntityNotFoundException, AccessDeniedException {
        Yeast yeast = yeastRepository.findById(change.getYeast().getId())
                .orElseThrow(() -> new EntityNotFoundException(Yeast.class, change.getYeast().getId()));
        UUID userId = change.getUser().getId();

        ensureUserIsAccessible(userId);
        change.setUser(getUserOrThrow(userId));
        change.setYeast(yeast);
        // if going bellow stock
        if (!stockSufficient.apply(change)) {
            throw new ConditionsNotMetException("Insufficient yeast stock");
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
            throw new AccessDeniedException("Cannot access yeast changes of user with ID %s".formatted(userId));
        }
    }
}
