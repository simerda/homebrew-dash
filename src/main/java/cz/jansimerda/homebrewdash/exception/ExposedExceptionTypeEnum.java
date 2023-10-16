package cz.jansimerda.homebrewdash.exception;

public enum ExposedExceptionTypeEnum {
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    CONDITIONS_NOT_MET("CONDITIONS_NOT_MET"),
    USER_UNAUTHENTICATED("USER_UNAUTHENTICATED");

    private final String text;

    ExposedExceptionTypeEnum(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
