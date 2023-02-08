package com.svetopolk.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class TimeZoneValidator implements ConstraintValidator<TimeZone, String> {

    private static final Set<String> set = Set.of(java.util.TimeZone.getAvailableIDs());

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        return set.contains(value);
    }
}
