package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class FilmsGetTests {
    private FilmController filmController;
    private Validator validator;

    @AfterEach
    void afterEach() {
        filmController.getFilms().clear();
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFindAllFilmsWhenNoFilmsAdded() {
        Collection<Film> films = filmController.findAllFilms();

        Assertions.assertTrue(films.isEmpty());
    }

    @Test
    void testFindAllFilmsWhen3FilmsAdded() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(1996, 7, 2))
                .duration(120)
                .build();

        Film film3 = Film.builder()
                .id(3L)
                .name("Фильм 3")
                .description("Описание фильма 3")
                .releaseDate(LocalDate.of(1984, 11, 18))
                .duration(150)
                .build();

        Map<Long, Film> expectedMap = new HashMap<>();
        expectedMap.put(film1.getId(), film1);
        expectedMap.put(film2.getId(), film2);
        expectedMap.put(film3.getId(), film3);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(film1, "film");
        Set<ConstraintViolation<Film>> violations1 = validator.validate(film1);

        for (ConstraintViolation<Film> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(film2, "film");
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);

        for (ConstraintViolation<Film> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(film3, "film");
        Set<ConstraintViolation<Film>> violations3 = validator.validate(film3);

        for (ConstraintViolation<Film> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        filmController.addFilm(film3, bindingResult3);

        Collection<Film> films = filmController.findAllFilms();

        Assertions.assertIterableEquals(expectedMap.values(), films);
    }
}
