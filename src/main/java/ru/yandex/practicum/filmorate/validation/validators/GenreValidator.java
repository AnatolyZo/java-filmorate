package ru.yandex.practicum.filmorate.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.validation.annotations.IsCorrectGenre;

import java.util.ArrayList;
import java.util.List;

public class GenreValidator implements ConstraintValidator<IsCorrectGenre, List<Genre>> {
    private final List<Long> listOfGenreIds = new ArrayList<>();

    @Override
    public void initialize(IsCorrectGenre constraintAnnotation) {
        for (Genres genre : Genres.values()) {
            listOfGenreIds.add((long) genre.ordinal() + 1);
        }
    }

    @Override
    public boolean isValid(List<Genre> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.stream().allMatch(genre -> listOfGenreIds.contains(genre.getId()));
    }
}
