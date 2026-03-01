package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class FilmsGetTests {
    @Autowired
    private FilmController filmController;

    @AfterEach
    void afterEach() {
        filmController.getFilms().clear();
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
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

        filmController.getFilms().put(film1.getId(), film1);
        filmController.getFilms().put(film2.getId(), film2);
        filmController.getFilms().put(film3.getId(), film3);

        Collection<Film> films = filmController.findAllFilms();

        Assertions.assertIterableEquals(expectedMap.values(), films);
    }
}
