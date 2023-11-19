package cz.jansimerda.homebrewdash.exception.internal.meross;

public class DeviceOfflineMerossException extends MerossException {
    /**
     * @inheritDoc
     */
    public DeviceOfflineMerossException(String message) {
        super(message);
    }

    /**
     * @inheritDoc
     */
    public DeviceOfflineMerossException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @inheritDoc
     */
    public DeviceOfflineMerossException(Throwable cause) {
        super(cause);
    }
}
