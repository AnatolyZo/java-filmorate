package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.validators.FilmCounterValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(long filmId, long userId) {
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);
        Film film = filmStorage.getFilm(filmId);

        film.setNewLike(userId);
        log.debug("Фильму с id {} добавлен лайк от пользователя с id {}", filmId, userId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);
        Film film = filmStorage.getFilm(filmId);

        film.deleteLike(userId);
        log.debug("У фильма с id {} удален лайк от пользователя с id {}", filmId, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        FilmCounterValidator.validate(count);
        List<Film> sortedFilms = filmStorage.sortFilms();
        //Обработка случая, если указанное количество выводимых фильмов превышает общее количество фильмов
        int upperEdge = Math.min(sortedFilms.size(), count);
        return sortedFilms.subList(0, upperEdge);
    }
}
