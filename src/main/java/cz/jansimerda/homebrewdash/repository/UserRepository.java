package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.email = :email OR (u.username IS NOT NULL AND u.username = :username)")
    Optional<User> findFirstByEmailOrUsername(String email, String username);
}
