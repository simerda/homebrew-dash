package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.authentication.CustomUserDetails;
import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.helpers.AuthenticationHelper;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends AbstractCrudService<User, UUID> {

    private final UserRepository repository;

    UserService(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public User create(User entity) {
        Optional<User> existing = repository.getFirstByEmailOrUsername(entity.getEmail(), entity.getUsername());
        enforceConstraints(existing, entity);

        return super.create(entity);
    }

    @Override
    public User update(User entity) throws EntityNotFoundException {
        Optional<User> existing = repository.getFirstByEmailOrUsernameExceptId(
                entity.getEmail(),
                entity.getUsername(),
                entity.getId()
        );
        enforceConstraints(existing, entity);

        return super.update(entity);
    }

    /**
     * Returns users accessible to logged-in user
     *
     * @return All users for admins, otherwise logged-in user.
     */
    public List<User> readAllAccessible() {
        CustomUserDetails details = AuthenticationHelper.getUserDetails();
        if (details.getUser().isAdmin()) {
            return readAll();
        }

        return repository.findById(details.getId()).stream().toList();
    }

    /**
     * Examines possible User match based on username and email,
     * compares it with candidate User and possibly throws appropriate exception
     *
     * @param match     the user with same email or username as candidate, empty if no match
     * @param candidate candidate user to be created/updated
     */
    private void enforceConstraints(Optional<User> match, User candidate) {
        if (match.isEmpty()) {
            return;
        }

        if (candidate.getEmail().equals(match.get().getEmail())) {
            throw new ConditionsNotMetException(
                    String.format("User with email %s already exists.", candidate.getEmail())
            );
        }

        throw new ConditionsNotMetException(
                String.format("User with username %s already exists.", candidate.getUsername())
        );
    }
}
