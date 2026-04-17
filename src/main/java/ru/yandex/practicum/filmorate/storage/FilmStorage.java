package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAllFilms();

    Optional<Film> findFilmById(long filmId);

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> findMostPopularFilms(int count);
}
