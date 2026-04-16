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
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;

public class FilmsPostTests {
    private FilmController filmController;
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmService(inMemoryFilmStorage);
    private Validator validator;

    @AfterEach
    void afterEach() {
        inMemoryFilmStorage.getFilms().clear();
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController(filmService);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testAddFilmWhen1FilmAdded() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto filmDto = FilmDto.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(filmDto, addedFilm);
    }

    @Test
    void testAddFilmWhen3FilmsAdded() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film1 = NewFilmRequest.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        NewFilmRequest film2 = NewFilmRequest.builder()
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(1996, 7, 2))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();

        NewFilmRequest film3 = NewFilmRequest.builder()
                .name("Фильм 3")
                .description("Описание фильма 3")
                .releaseDate(LocalDate.of(1984, 11, 18))
                .duration(150)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto film1Dto = FilmDto.builder()
                .id(1L)
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto film2Dto = FilmDto.builder()
                .id(2L)
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(1996, 7, 2))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto film3Dto = FilmDto.builder()
                .id(3L)
                .name("Фильм 3")
                .description("Описание фильма 3")
                .releaseDate(LocalDate.of(1984, 11, 18))
                .duration(150)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(film1, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations1 = validator.validate(film1);

        for (ConstraintViolation<NewFilmRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto addedFilm1 = filmController.addFilm(film1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(film2, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations2 = validator.validate(film2);

        for (ConstraintViolation<NewFilmRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto addedFilm2 = filmController.addFilm(film2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(film3, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations3 = validator.validate(film3);

        for (ConstraintViolation<NewFilmRequest> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        FilmDto addedFilm3 = filmController.addFilm(film3, bindingResult3);

        Assertions.assertEquals(film1Dto, addedFilm1);
        Assertions.assertEquals(film2Dto, addedFilm2);
        Assertions.assertEquals(film3Dto, addedFilm3);
    }

    @Test
    void testAddFilmWithEmptyName() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
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
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto filmDto = FilmDto.builder()
                .id(1L)
                .name("Фильм")
                .description(new String((new char[200])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(filmDto, addedFilm);
    }

    @Test
    void testAddFilmWithDescriptionEquals201Characters() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description(new String((new char[201])))
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
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
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto filmDto = FilmDto.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(filmDto, addedFilm);
    }

    @Test
    void testAddFilmWithReleaseDateEquals27December1895() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateValue(NewFilmRequest.class, "releaseDate",
                LocalDate.of(1895, 12, 27));

        for (ConstraintViolation<NewFilmRequest> violation : violations) {
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
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(1)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDto filmDto = FilmDto.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(1)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto addedFilm = filmController.addFilm(film, bindingResult);

        Assertions.assertEquals(filmDto, addedFilm);
    }

    @Test
    void testAddFilmWithDurationEquals0() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(0)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateValue(NewFilmRequest.class, "duration", 0);

        for (ConstraintViolation<NewFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film, bindingResult));
    }
}
