package com.posadskiy.skillrepeater.api.validation;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.ChronoUnit;
import java.util.Set;

@Singleton
public class PeriodValidator implements ConstraintValidator<SkillPeriod, ChronoUnit> {

    private static final Set<ChronoUnit> ALLOWED = Set.of(
            ChronoUnit.HOURS,
            ChronoUnit.DAYS,
            ChronoUnit.WEEKS,
            ChronoUnit.MONTHS,
            ChronoUnit.YEARS
    );

    @Override
    public boolean isValid(ChronoUnit value, ConstraintValidatorContext context) {
        return value == null || ALLOWED.contains(value);
    }
}
