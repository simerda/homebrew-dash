package cz.jansimerda.homebrewdash.exception.exposed;

public abstract class ExposedException extends RuntimeException {
    public ExposedException(String message) {
        super(message);
    }

    public ExposedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @return exposed exception type enum
     */
    public abstract ExposedExceptionTypeEnum getType();
}
