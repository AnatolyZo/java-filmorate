package ru.yandex.practicum.filmorate.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validation.annotations.IsDateAfter;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<IsDateAfter, LocalDate> {
    private LocalDate dateToCompare;

    @Override
    public void initialize(IsDateAfter constraintAnnotation) {
        dateToCompare = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.isAfter(dateToCompare);
    }
}
