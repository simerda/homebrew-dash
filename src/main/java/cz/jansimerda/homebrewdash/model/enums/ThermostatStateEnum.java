package cz.jansimerda.homebrewdash.model.enums;

public enum ThermostatStateEnum {
    WAITING_FOR_HYDROMETER(0),
    READY(1),
    ACTIVE(2),
    TEMP_READ_ERROR(3),
    SERVICE_ERROR(4);

    private final int value;

    ThermostatStateEnum(int value) {
        this.value = value;
    }

    /**
     * @return internal brew state enum value
     */
    public int getValue() {
        return value;
    }
}
