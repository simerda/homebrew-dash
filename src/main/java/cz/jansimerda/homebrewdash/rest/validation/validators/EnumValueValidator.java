package cz.jansimerda.homebrewdash.rest.validation.validators;

import cz.jansimerda.homebrewdash.rest.validation.constraints.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private List<String> acceptedValues;

    private EnumValue annotation;

    @Override
    public void initialize(EnumValue annotation) {
        this.annotation = annotation;
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        annotation.message().replace("{enumValues}", String.join(", ", acceptedValues))
                )
                .addConstraintViolation();


        return acceptedValues.contains(value);
    }
}
