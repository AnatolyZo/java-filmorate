package ru.yandex.practicum.filmorate.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validation.annotations.IsDateBefore;

import java.time.LocalDate;

public class BirthdayValidator implements ConstraintValidator<IsDateBefore, LocalDate> {
    private LocalDate dateToCompare;

    @Override
    public void initialize(IsDateBefore constraintAnnotation) {
        dateToCompare = LocalDate.now().plusDays(1);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.isBefore(dateToCompare);
    }
}
