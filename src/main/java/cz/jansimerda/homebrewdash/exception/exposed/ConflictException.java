package cz.jansimerda.homebrewdash.exception.exposed;

public class ConflictException extends ExposedException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public ExposedExceptionTypeEnum getType() {
        return ExposedExceptionTypeEnum.CONFLICT;
    }
}
