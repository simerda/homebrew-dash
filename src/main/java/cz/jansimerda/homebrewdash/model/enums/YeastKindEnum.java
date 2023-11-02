package cz.jansimerda.homebrewdash.model.enums;

public enum YeastKindEnum {
    DRIED(0),
    LIQUID(1);

    private final int value;

    YeastKindEnum(int value) {
        this.value = value;
    }

    /**
     * @return internal representation of the yeast kind
     */
    public int getValue() {
        return value;
    }
}
