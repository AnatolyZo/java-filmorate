package ru.yandex.practicum.filmorate.validation.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@Slf4j
public class FilmCounterValidator {
    public static void validate(int count) {
        if (count < 0) {
            log.info("Количество выводимых фильмов должно быть больше нуля");
            throw new ValidationException("Количество выводимых фильмов должно быть больше нуля");
        }
    }
}
