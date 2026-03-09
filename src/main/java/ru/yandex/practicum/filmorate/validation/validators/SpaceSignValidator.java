package ru.yandex.practicum.filmorate.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validation.annotations.IsContainsSpaceSigns;

public class SpaceSignValidator implements ConstraintValidator<IsContainsSpaceSigns, String> {
    private static final String SPACE_SIGN = " ";

    @Override
    public void initialize(IsContainsSpaceSigns constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !value.contains(SPACE_SIGN);
    }
}
