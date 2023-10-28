package cz.jansimerda.homebrewdash.rest.validation.constraints;

import cz.jansimerda.homebrewdash.rest.validation.validators.NotZeroValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotZeroValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotZero {
    String message() default "Number most not be zero";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
