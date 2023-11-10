package cz.jansimerda.homebrewdash.rest.exception;

import cz.jansimerda.homebrewdash.exception.ExposedExceptionTypeEnum;

public class RequestParsingErrorResponse extends ErrorResponse {

    private final String details;

    public RequestParsingErrorResponse(String message, String details) {
        super(ExposedExceptionTypeEnum.PARSING_ERROR, message);
        this.details = details;
    }

    /**
     * Get details regarding
     *
     * @return detailed meesage
     */
    public String getDetails() {
        return details;
    }
}
