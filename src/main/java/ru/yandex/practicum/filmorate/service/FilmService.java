package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.validators.FilmCounterValidator;
import ru.yandex.practicum.filmorate.validation.validators.IdValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Film addLike(long filmId, long userId) {
        IdValidator.validate(inMemoryFilmStorage, filmId);
        IdValidator.validate(inMemoryUserStorage, userId);
        Film film = inMemoryFilmStorage.getFilm(filmId);

        film.setNewLike(userId);
        log.debug("Фильму с id {} добавлен лайк от пользователя с id {}", filmId, userId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        IdValidator.validate(inMemoryFilmStorage, filmId);
        IdValidator.validate(inMemoryUserStorage, userId);
        Film film = inMemoryFilmStorage.getFilm(filmId);

        film.deleteLike(userId);
        log.debug("У фильма с id {} удален лайк от пользователя с id {}", filmId, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        FilmCounterValidator.validate(count);
        List<Film> sortedFilms = inMemoryFilmStorage.sortFilms();
        //Обработка случая, если указанное количество выводимых фильмов превышает общее количество фильмов
        int upperEdge = Math.min(sortedFilms.size(), count);
        return sortedFilms.subList(0, upperEdge);
    }
}
