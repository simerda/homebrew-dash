package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> getFirstByEmailOrUsername(String email, String username);

    @Query("SELECT u FROM User u WHERE u.id != :id AND (u.email = :email OR u.username = :username)")
    Optional<User> getFirstByEmailOrUsernameExceptId(String email, String username, UUID id);

    Optional<User> getFirstByEmail(String email);

    Optional<User> getFirstByUsername(String username);
}
