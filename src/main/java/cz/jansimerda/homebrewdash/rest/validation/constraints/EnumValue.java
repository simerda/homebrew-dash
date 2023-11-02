package cz.jansimerda.homebrewdash.rest.validation.constraints;

import cz.jansimerda.homebrewdash.rest.validation.validators.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass();

    String message() default "Must be any of {enumValues}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
