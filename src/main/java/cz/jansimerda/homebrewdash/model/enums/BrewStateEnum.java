package cz.jansimerda.homebrewdash.model.enums;

public enum BrewStateEnum implements Comparable<BrewStateEnum> {
    PLANNING(0),
    BREWING(1),
    FERMENTING(2),
    MATURING(3),
    DONE(4),
    BOTCHED(5);

    private final int value;

    BrewStateEnum(int value) {
        this.value = value;
    }

    /**
     * @return internal brew state enum value
     */
    public int getValue() {
        return value;
    }
}
