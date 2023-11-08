package cz.jansimerda.homebrewdash.model.enums;

public enum TemperatureUnitEnum {
    K(0),
    C(1),
    F(2);

    private final int value;

    TemperatureUnitEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
