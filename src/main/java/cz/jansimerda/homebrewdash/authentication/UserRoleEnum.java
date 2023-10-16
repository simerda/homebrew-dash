package cz.jansimerda.homebrewdash.authentication;

public enum UserRoleEnum {
    ADMIN("ADMIN");

    private final String textRole;

    UserRoleEnum(String textRole) {
        this.textRole = textRole;
    }

    @Override
    public String toString() {
        return textRole;
    }
}
