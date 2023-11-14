package cz.jansimerda.homebrewdash.exception.exposed;

public enum ExposedExceptionTypeEnum {
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    PARSING_ERROR("PARSING_ERROR"),
    CONDITIONS_NOT_MET("CONDITIONS_NOT_MET"),
    USER_UNAUTHENTICATED("USER_UNAUTHENTICATED"),
    ACCESS_DENIED("ACCESS_DENIED"),
    CONFLICT("CONFLICT");

    private final String text;

    ExposedExceptionTypeEnum(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
