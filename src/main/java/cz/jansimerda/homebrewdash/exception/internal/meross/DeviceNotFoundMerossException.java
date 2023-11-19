package cz.jansimerda.homebrewdash.exception.internal.meross;

public class DeviceNotFoundMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public DeviceNotFoundMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public DeviceNotFoundMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public DeviceNotFoundMerossException(Throwable cause) {
        super(cause);
    }
}
