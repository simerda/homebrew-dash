package cz.jansimerda.homebrewdash.rest.validation.validators;

import cz.jansimerda.homebrewdash.rest.validation.constraints.NotZero;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotZeroValidator implements ConstraintValidator<NotZero, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || value != 0;
    }
}
