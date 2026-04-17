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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

class FilmsGetTests {
    private FilmController filmController;
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final FilmService filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
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
    void testFindAllFilmsWhenNoFilmsAdded() {
        Collection<FilmDto> films = filmController.findAllFilms();

        Assertions.assertTrue(films.isEmpty());
    }

    @Test
    void testFindAllFilmsWhen3FilmsAdded() {
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

        Map<Long, FilmDto> expectedMap = new HashMap<>();
        expectedMap.put(1L, film1Dto);
        expectedMap.put(2L, film2Dto);
        expectedMap.put(3L, film3Dto);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(film1, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations1 = validator.validate(film1);

        for (ConstraintViolation<NewFilmRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(film2, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations2 = validator.validate(film2);

        for (ConstraintViolation<NewFilmRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        filmController.addFilm(film2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(film3, "film");
        Set<ConstraintViolation<NewFilmRequest>> violations3 = validator.validate(film3);

        for (ConstraintViolation<NewFilmRequest> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        filmController.addFilm(film3, bindingResult3);

        Collection<FilmDto> films = filmController.findAllFilms();

        Assertions.assertIterableEquals(expectedMap.values(), films);
    }
}
