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
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilmsPostTests {
    private FilmController filmController;
    private InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private FilmService filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
    private Validator validator;

    @AfterEach
    void afterEach() {
        inMemoryFilmStorage.getFilms().clear();
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController(inMemoryFilmStorage, filmService);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testAddFilmWhen1FilmAdded() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(film, addedFilm);
    }

    @Test
    void testAddFilmWhen3FilmsAdded() {
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

        Film addedFilm1 = filmController.addFilm(film1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(film2, "film");
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);

        for (ConstraintViolation<Film> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film addedFilm2 = filmController.addFilm(film2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(film3, "film");
        Set<ConstraintViolation<Film>> violations3 = validator.validate(film3);

        for (ConstraintViolation<Film> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        Film addedFilm3 = filmController.addFilm(film3, bindingResult3);

        Assertions.assertEquals(film1, addedFilm1);
        Assertions.assertEquals(film2, addedFilm2);
        Assertions.assertEquals(film3, addedFilm3);
        Assertions.assertIterableEquals(expectedMap.values(), inMemoryFilmStorage.getFilms().values());
    }

    @Test
    void testAddFilmWithEmptyName() {
        Film film = Film.builder()
                .id(1L)
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class, "name", null);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film, bindingResult));
    }

    @Test
    void testAddFilmWithDescriptionEquals200Characters() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(film, addedFilm);
    }

    @Test
    void testAddFilmWithDescriptionEquals201Characters() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[201])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
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

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film, bindingResult));
    }

    @Test
    void testAddFilmWithReleaseDateEquals28December1895() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(film, addedFilm);
    }

    @Test
    void testAddFilmWithReleaseDateEquals27December1895() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class, "releaseDate",
                LocalDate.of(1895, 12, 27));

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film, bindingResult));
    }

    @Test
    void testAddFilmWithDurationEquals1() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(1)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Film addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(film, addedFilm);
    }

    @Test
    void testAddFilmWithDurationEquals0() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(0)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<Film>> violations = validator.validateValue(Film.class, "duration", 0);

        for (ConstraintViolation<Film> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film, bindingResult));
    }
}
