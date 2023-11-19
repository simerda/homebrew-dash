package cz.jansimerda.homebrewdash.exception.internal.meross;

public class GeneralMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public GeneralMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public GeneralMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public GeneralMerossException(Throwable cause) {
        super(cause);
    }
}
