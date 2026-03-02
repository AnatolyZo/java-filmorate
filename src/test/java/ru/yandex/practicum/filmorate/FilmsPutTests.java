package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

public class FilmsPutTests {
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
    void testUpdateFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        filmController.getFilms().put(film.getId(), film);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(updateToFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithEmptyName() {
        Film film = Film.builder()
                .id(1L)
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        filmController.getFilms().put(film.getId(), film);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class, "name", null);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(updateToFilm, bindingResult));
    }

    @Test
    void testUpdateFilmWithDescriptionEquals200Characters() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description(new String(new char[200]))
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<Film> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(updateToFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithDescriptionEquals201Characters() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description(new String(new char[201]))
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<Film> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class,
                "description",
                new String((new char[201])));

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(updateToFilm, bindingResult));
    }

    @Test
    void testUpdateFilmWithReleaseDateEquals28December1895() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(101)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<Film> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(updateToFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithReleaseDateEquals27December1895() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(101)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<Film> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class, "releaseDate",
                LocalDate.of(1895, 12, 27));

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(updateToFilm, bindingResult));
    }

    @Test
    void testUpdateFilmWithDuration1() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(1)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<Film> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(updateToFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithDuration0() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        Film updateToFilm = Film.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(0)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<Film> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class, "duration", 0);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(updateToFilm, bindingResult));
    }
}
