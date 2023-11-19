package cz.jansimerda.homebrewdash.exception.exposed;

public class ServiceUnavailableException extends ExposedException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public ExposedExceptionTypeEnum getType() {
        return ExposedExceptionTypeEnum.SERVICE_UNAVAILABLE;
    }
}
