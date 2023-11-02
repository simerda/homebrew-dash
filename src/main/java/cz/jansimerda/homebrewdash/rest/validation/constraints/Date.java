package cz.jansimerda.homebrewdash.rest.validation.constraints;

import cz.jansimerda.homebrewdash.rest.validation.validators.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Date {
    String DATE_NOW = "DATE_NOW";

    String before() default "";

    String after() default "";

    boolean beforeInclusive() default false;

    boolean afterInclusive() default true;

    String message() default "Must by a valid date in the Y-m-d format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
