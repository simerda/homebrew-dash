package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.ConditionsNotMetException;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        Optional<User> existing = this.repository.getFirstByEmailOrUsername(entity.getEmail(), entity.getUsername());

        if (existing.isPresent() && entity.getEmail().equals(existing.get().getEmail())) {
            throw new ConditionsNotMetException(String.format("User with email %s already exists.", entity.getEmail()));
        }

        if (existing.isPresent()) {
            throw new ConditionsNotMetException(String.format("User with username %s already exists.", entity.getUsername()));
        }

        return super.create(entity);
    }
}
