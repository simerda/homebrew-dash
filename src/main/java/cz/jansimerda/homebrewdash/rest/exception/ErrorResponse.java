package cz.jansimerda.homebrewdash.rest.exception;

import cz.jansimerda.homebrewdash.exception.exposed.ExposedExceptionTypeEnum;

public class ErrorResponse {

    private final ExposedExceptionTypeEnum errorType;

    private final String message;

    public ErrorResponse(ExposedExceptionTypeEnum errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    /**
     * Get String identifier of error type
     *
     * @return error type
     */
    public String getType() {
        return errorType.toString();
    }

    /**
     * @return error message
     */
    public String getMessage() {
        return message;
    }
}
