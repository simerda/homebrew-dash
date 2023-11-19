package cz.jansimerda.homebrewdash.exception.internal.meross;

public class AuthenticationMissingMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public AuthenticationMissingMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public AuthenticationMissingMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public AuthenticationMissingMerossException(Throwable cause) {
        super(cause);
    }
}
