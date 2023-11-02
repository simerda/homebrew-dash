package cz.jansimerda.homebrewdash.model.enums;

public enum YeastTypeEnum {
    LAGER(0),
    ALE(1);

    private final int value;

    YeastTypeEnum(int value) {
        this.value = value;
    }

    /**
     * @return internal representation of the yeast type
     */
    public int getValue() {
        return value;
    }
}
