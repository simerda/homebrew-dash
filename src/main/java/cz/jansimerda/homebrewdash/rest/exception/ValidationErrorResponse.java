package cz.jansimerda.homebrewdash.rest.exception;

import cz.jansimerda.homebrewdash.exception.ExposedExceptionTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {
    private final List<Violation> errors = new ArrayList<>();

    public ValidationErrorResponse(ExposedExceptionTypeEnum errorType, String message) {
        super(errorType, message);
    }

    public void addError(Violation error) {
        errors.add(error);
    }

    public List<Violation> getErrors() {
        return errors;
    }
}
