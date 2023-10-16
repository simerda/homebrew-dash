package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> getFirstByEmailOrUsername(String email, String username);

    Optional<User> getFirstByEmail(String email);

    Optional<User> getFirstByUsername(String username);
}
