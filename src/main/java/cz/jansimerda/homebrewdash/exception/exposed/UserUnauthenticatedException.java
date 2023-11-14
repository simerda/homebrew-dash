package cz.jansimerda.homebrewdash.exception.exposed;

public class UserUnauthenticatedException extends ExposedException {
    public UserUnauthenticatedException(String message) {
        super(message);
    }

    public UserUnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public ExposedExceptionTypeEnum getType() {
        return ExposedExceptionTypeEnum.USER_UNAUTHENTICATED;
    }
}
