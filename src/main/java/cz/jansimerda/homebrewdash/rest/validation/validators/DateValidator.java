package cz.jansimerda.homebrewdash.rest.validation.validators;

import cz.jansimerda.homebrewdash.rest.validation.constraints.Date;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<Date, String> {
    private boolean beforeInclusive = false;

    private boolean afterInclusive = false;

    private LocalDate before;

    private LocalDate after;

    @Override
    public void initialize(Date annotation) {
        beforeInclusive = annotation.beforeInclusive();
        afterInclusive = annotation.afterInclusive();

        if (annotation.before() != null && !annotation.before().isEmpty()) {
            before = annotation.before().equals(Date.DATE_NOW)
                    ? LocalDate.now()
                    : LocalDate.parse(annotation.before(), DateTimeFormatter.ISO_LOCAL_DATE);
        }

        if (annotation.after() != null && !annotation.after().isEmpty()) {
            after = annotation.after().equals(Date.DATE_NOW)
                    ? LocalDate.now()
                    : LocalDate.parse(annotation.after(), DateTimeFormatter.ISO_LOCAL_DATE);
        }

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }


        LocalDate date;
        try {
            date = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return false;
        }

        // ensure is before
        if (before != null && (date.isAfter(before) || (!beforeInclusive && date.isEqual(before)))) {

            setMessage(context, "Must be before%s %s".formatted(
                    beforeInclusive ? " or equal to" : "",
                    before.toString()
            ));
            return false;
        }

        // ensure is after
        if (after != null && (date.isBefore(after) || (!afterInclusive && date.isEqual(after)))) {

            setMessage(context, "Must be after%s %s".formatted(
                    afterInclusive ? " or equal to" : "",
                    after.toString()
            ));
            return false;
        }

        return true;
    }

    /**
     * Sets validation error message
     *
     * @param context validation context
     * @param message message to be set
     */
    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
