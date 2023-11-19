package cz.jansimerda.homebrewdash.exception.internal.meross;

public class InvalidCredentialsMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public InvalidCredentialsMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public InvalidCredentialsMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public InvalidCredentialsMerossException(Throwable cause) {
        super(cause);
    }
}
