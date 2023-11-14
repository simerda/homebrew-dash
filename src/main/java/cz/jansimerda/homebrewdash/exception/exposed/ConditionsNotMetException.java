package cz.jansimerda.homebrewdash.exception.exposed;

public class ConditionsNotMetException extends ExposedException {

    public ConditionsNotMetException(String message) {
        super(message);
    }

    @Override
    public ExposedExceptionTypeEnum getType() {
        return ExposedExceptionTypeEnum.CONDITIONS_NOT_MET;
    }
}
