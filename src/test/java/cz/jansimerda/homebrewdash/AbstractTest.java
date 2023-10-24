package cz.jansimerda.homebrewdash;

import cz.jansimerda.homebrewdash.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AbstractTest {
    public final String ADMIN_PASSWORD = "SecretPass+000";
    public final String USER_PASSWORD = "(dummyPass)23";

    protected User createAdmin() {
        User admin = createDummyUser();
        admin.setEmail("admin@mail.com");
        admin.setPassword("{bcrypt}$2a$10$YXqQicmiU1hl8tcIDn/Oie/1ecjMpogN9eRuVGctAkgdYIEm8Cl3i");
        admin.setUsername("admin");
        admin.setFirstName(null);
        admin.setSurname(null);
        admin.setIsAdmin(true);

        return admin;
    }

    protected User createUser() {
        User user = createDummyUser();
        user.setEmail("user@mail.com");
        user.setPassword("{bcrypt}$2a$10$HwfwQmycTmwjLJDitn3vdusXOkI238eAoYTQmgzuloXa9sQhnvA8u");
        user.setUsername("user");
        user.setFirstName("John");
        user.setSurname("Doe");
        user.setIsAdmin(false);

        return user;
    }

    protected User createDummyUser() {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail("duser@mail.com");
        user.setPassword("(dummyPass)23");
        user.setUsername("user123");
        user.setFirstName("John");
        user.setSurname("Doe");
        user.setIsAdmin(false);
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }
}
