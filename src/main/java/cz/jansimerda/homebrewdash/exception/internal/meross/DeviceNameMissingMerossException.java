package cz.jansimerda.homebrewdash.exception.internal.meross;

public class DeviceNameMissingMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public DeviceNameMissingMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public DeviceNameMissingMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public DeviceNameMissingMerossException(Throwable cause) {
        super(cause);
    }
}
