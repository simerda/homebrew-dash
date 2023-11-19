package cz.jansimerda.homebrewdash.exception.internal.meross;

public abstract class MerossException extends Exception {
    public MerossException(String message) {
        super(message);
    }

    public MerossException(String message, Throwable cause) {
        super(message, cause);
    }

    public MerossException(Throwable cause) {
        super(cause);
    }
}
