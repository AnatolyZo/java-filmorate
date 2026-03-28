package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmLikes;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.validators.FilmCounterValidator;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Set<FilmLikes> filmLikesList = new HashSet<>();
    private final Set<FilmGenre> filmGenreList = new HashSet<>();

    public Film addLike(long filmId, long userId) {
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);

        filmLikesList.add(new FilmLikes(filmId, userId));
        log.debug("Фильму с id {} добавлен лайк от пользователя с id {}", filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public Film deleteLike(long filmId, long userId) {
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);

        filmLikesList.remove(new FilmLikes(filmId, userId));
        log.debug("У фильма с id {} удален лайк от пользователя с id {}", filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        FilmCounterValidator.validate(count);

        //Собираем Map: ключ - id фильма, значение - количество повторений id (что эквивалентно количеству лайков)
        Map<Long, Integer> filmsLikesMap = filmLikesList.stream()
                .collect(Collectors.toMap(
                        FilmLikes::getFilmId,
                        element-> 1,
                        (existingValue, newValue) -> existingValue + 1
                ));

        //Сортировка полученной Map по значениям
        Map<Long, Integer> sortedFilms = filmsLikesMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        //Получение списка наиболее популярных фильмов
        List<Film> popularFilms = sortedFilms.keySet().stream()
                .map(filmStorage::getFilm)
                .toList();

        //Обработка случая, если указанное количество выводимых фильмов превышает общее количество фильмов
        int upperEdge = Math.min(sortedFilms.size(), count);
        return popularFilms.subList(0, upperEdge);
    }

    public Film addGenre(long filmId, Genre genre) {
        filmStorage.validateId(filmId);

        filmGenreList.add(new FilmGenre(filmId, genre));
        log.debug("Фильму с id {} добавлен жанр {}", filmId, genre);
        return filmStorage.getFilm(filmId);
    }

    public Film deleteGenre(long filmId, Genre genre) {
        filmStorage.validateId(filmId);

        filmGenreList.remove(new FilmGenre(filmId, genre));
        log.debug("У фильма с id {} удален жанр {}", filmId, genre);
        return filmStorage.getFilm(filmId);
    }

    public List<Genre> showFilmGenres(long filmId, Genre genre) {
        filmStorage.validateId(filmId);

        return filmGenreList.stream()
                .filter(pair -> pair.getFilmId() == filmId)
                .map(FilmGenre::getGenre)
                .toList();
    }
}
