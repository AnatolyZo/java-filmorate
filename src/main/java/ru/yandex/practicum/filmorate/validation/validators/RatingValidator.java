package ru.yandex.practicum.filmorate.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.annotations.IsCorrectMPARating;

import java.util.ArrayList;
import java.util.List;

public class RatingValidator implements ConstraintValidator<IsCorrectMPARating, Mpa> {
    private final List<Long> listOfRatingIds = new ArrayList<>();

    @Override
    public void initialize(IsCorrectMPARating constraintAnnotation) {
        for (MpaRating rating : MpaRating.values()) {
            listOfRatingIds.add((long) rating.ordinal() + 1);
        }
    }

    @Override
    public boolean isValid(Mpa value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return listOfRatingIds.contains(value.getId());
    }
}
