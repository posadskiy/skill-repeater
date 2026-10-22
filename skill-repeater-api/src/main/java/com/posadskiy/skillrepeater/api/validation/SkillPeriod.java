package com.posadskiy.skillrepeater.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PeriodValidator.class)
public @interface SkillPeriod {
    String message() default "Period must be HOURS, DAYS, WEEKS, MONTHS, or YEARS";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
