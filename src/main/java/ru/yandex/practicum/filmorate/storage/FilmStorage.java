package ru.yandex.practicum.filmorate.storage;

import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> findAllFilms();

    Film addFilm(Film film, BindingResult bindingResult);

    Film updateFilm(Film newFilm, BindingResult bindingResult);

    Film getFilm(long filmId);

    List<Film> sortFilms();

    void validateId(long filmId);
}
