package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationResults;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<FilmDto> findAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto findFilmById(long filmId) {
        return filmStorage.findFilmById(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с ID %d не найден", filmId)));
    }

    public FilmDto addFilm(NewFilmRequest newFilmRequest, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        setGenresToFilm(film);
        setMpaRatingToFilm(film);
        film = filmStorage.addFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest updateFilmRequest, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        Film updatingFilm = filmStorage.findFilmById(updateFilmRequest.getId())
                .map(film -> FilmMapper.updateFilmFields(film, updateFilmRequest))
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с ID %d не найден", updateFilmRequest.getId())));


        updatingFilm = filmStorage.updateFilm(updatingFilm);
        return FilmMapper.mapToFilmDto(updatingFilm);
    }

    public void addLike(long filmId, long userId) {
        validateId(filmId, userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        validateId(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.findMostPopularFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private void setGenresToFilm(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres().stream()
                    .map(genre -> {
                        Genre newGenre = new Genre();
                        newGenre.setId(genre.getId());
                        newGenre.setName(Genres.getDescription(genre.getId()));
                        return newGenre;
                    })
                    .distinct()
                    .toList();

            film.setGenres(genres);
        }
    }

    private void setMpaRatingToFilm(Film film) {
        if (film.getMpa() != null) {
            Mpa mpa = film.getMpa();
            film.getMpa().setName(MpaRating.getDescription(mpa.getId()));
        }
    }

    private void validateId(long filmId, long userId) {
        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> {
                    log.info("Фильм c id {} не найден", filmId);
                    return new NotFoundException(String.format("Фильм c id %d не найден", filmId));
                });

        userStorage.findUserById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь c id {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь c id %d не найден", userId));
                });
    }
}
