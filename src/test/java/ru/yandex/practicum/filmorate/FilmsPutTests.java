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
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilmsPutTests {
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
    void testUpdateFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        FilmDto expectedFilm = FilmDto.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        inMemoryFilmStorage.getFilms().put(film.getId(), film);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(expectedFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithEmptyName() {
        Film film = Film.builder()
                .id(1L)
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1989, 12, 12))
                .duration(100)
                .build();

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        inMemoryFilmStorage.getFilms().put(film.getId(), film);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validateValue(UpdateFilmRequest.class, "name", null);

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
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

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description(new String(new char[200]))
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        FilmDto expectedFilm = FilmDto.builder()
                .id(1L)
                .name("Новый фильм")
                .description(new String(new char[200]))
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(expectedFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithDescriptionEquals201Characters() {
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

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description(new String(new char[201]))
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(101)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validateValue(UpdateFilmRequest.class,
                "description",
                new String((new char[201])));

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
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

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(101)
                .build();

        FilmDto expectedFilm = FilmDto.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(101)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(expectedFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithReleaseDateEquals27December1895() {
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

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(101)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validateValue(UpdateFilmRequest.class, "releaseDate",
                LocalDate.of(1895, 12, 27));

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
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

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(1)
                .build();

        FilmDto expectedFilm = FilmDto.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(1)
                .mpa(mpa)
                .genres(genres)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateToFilm);

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        FilmDto updatedFilm = filmController.updateFilm(updateToFilm, bindingResult);

        Assertions.assertEquals(expectedFilm, updatedFilm);
    }

    @Test
    void testUpdateFilmWithDuration0() {
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

        UpdateFilmRequest updateToFilm = UpdateFilmRequest.builder()
                .id(1L)
                .name("Новый фильм")
                .description("Новое описание фильма")
                .releaseDate(LocalDate.of(1988, 11, 11))
                .duration(0)
                .build();

        BeanPropertyBindingResult bindingResultFilm = new BeanPropertyBindingResult(film, "film");
        Set<ConstraintViolation<NewFilmRequest>> violationsFilm = validator.validate(film);

        for (ConstraintViolation<NewFilmRequest> violation : violationsFilm) {
            bindingResultFilm.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film, bindingResultFilm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToFilm, "film");
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validateValue(UpdateFilmRequest.class, "duration", 0);

        for (ConstraintViolation<UpdateFilmRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(updateToFilm, bindingResult));
    }
}
