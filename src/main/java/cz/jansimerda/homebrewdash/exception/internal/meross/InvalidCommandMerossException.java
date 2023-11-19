package cz.jansimerda.homebrewdash.exception.internal.meross;

public class InvalidCommandMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public InvalidCommandMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public InvalidCommandMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public InvalidCommandMerossException(Throwable cause) {
        super(cause);
    }
}
