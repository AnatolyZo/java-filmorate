package ru.yandex.practicum.filmorate.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validation.annotations.IsCorrectGenre;

import java.util.List;

public class GenreValidator implements ConstraintValidator<IsCorrectGenre, List<Genre>> {
    private final List<Long> listOfGenreIds;

    public GenreValidator() {
        this.listOfGenreIds = List.of(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @Override
    public void initialize(IsCorrectGenre constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<Genre> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.stream().allMatch(genre -> listOfGenreIds.contains(genre.getId()));
    }
}
