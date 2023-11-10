package cz.jansimerda.homebrewdash.rest.exception;

import cz.jansimerda.homebrewdash.exception.ConflictException;
import cz.jansimerda.homebrewdash.exception.EntityNotFoundException;
import cz.jansimerda.homebrewdash.exception.ExposedException;
import cz.jansimerda.homebrewdash.exception.ExposedExceptionTypeEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ValidationErrorResponse handleConstraintValidationException(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse("The request body contains invalid data");
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.addError(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse("The request body contains invalid data");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.addError(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody RequestParsingErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new RequestParsingErrorResponse("The request body cannot be parsed", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorResponse(ExposedExceptionTypeEnum.ACCESS_DENIED, e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        return new ErrorResponse(e.getType(), e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleConflictException(ConflictException e) {
        return new ErrorResponse(e.getType(), e.getMessage());
    }

    @ExceptionHandler(ExposedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleExposedException(ExposedException e) {
        return new ErrorResponse(e.getType(), e.getMessage());
    }
}
