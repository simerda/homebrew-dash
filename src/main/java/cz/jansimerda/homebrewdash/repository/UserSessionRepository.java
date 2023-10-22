package cz.jansimerda.homebrewdash.repository;

import cz.jansimerda.homebrewdash.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> getFirstByToken(String token);

    @Query("SELECT us FROM UserSession us WHERE us.id = :id " +
            "AND (:withExpired = true OR us.expiresAt > current_timestamp)")
    Optional<UserSession> getFirstById(UUID id, boolean withExpired);

    @Query("SELECT us FROM UserSession us WHERE us.id = :id AND us.user.id = :userId " +
            "AND (:withExpired = true OR us.expiresAt > current_timestamp)")
    Optional<UserSession> getFirstByIdForUser(UUID id, UUID userId, boolean withExpired);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :id " +
            "AND (:withExpired = true OR us.expiresAt > current_timestamp)")
    List<UserSession> findByUserId(UUID id, boolean withExpired);

    @Query("SELECT us FROM UserSession us WHERE :withExpired = true OR us.expiresAt > current_timestamp")
    List<UserSession> findAll(boolean withExpired);
}
