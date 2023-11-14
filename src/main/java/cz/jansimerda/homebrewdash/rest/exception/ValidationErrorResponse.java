package cz.jansimerda.homebrewdash.rest.exception;

import cz.jansimerda.homebrewdash.exception.exposed.ExposedExceptionTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {
    private final List<Violation> errors = new ArrayList<>();

    public ValidationErrorResponse(String message) {
        super(ExposedExceptionTypeEnum.VALIDATION_ERROR, message);
    }

    public void addError(Violation error) {
        errors.add(error);
    }

    public List<Violation> getErrors() {
        return errors;
    }
}
