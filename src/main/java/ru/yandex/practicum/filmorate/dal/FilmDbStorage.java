package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_FILMS_QUERY = "SELECT\n" +
        "    f.film_id,\n" +
        "    f.name,\n" +
        "    f.description,\n" +
        "    f.release_date,\n" +
        "    f.duration,\n" +
        "    r.rating_id AS mpa_id,\n" +
        "    r.rating AS mpa_name,\n" +
        "    GROUP_CONCAT(CONCAT(g.genre_id, ' ', g.genre) ORDER BY g.genre_id SEPARATOR ', ') AS genre_list\n" +
        "FROM films f\n" +
        "JOIN ratings AS r ON f.rating_id = r.rating_id\n" +
        "LEFT JOIN films_genres fg ON f.film_id = fg.film_id\n" +
        "LEFT JOIN genres g ON fg.genre_id = g.genre_id\n" +
        "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO films_genres(film_id, genre_id) VALUES(?, ?)";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT\n" +
            "    f.film_id,\n" +
            "    f.name,\n" +
            "    f.description,\n" +
            "    f.release_date,\n" +
            "    f.duration,\n" +
            "    r.rating_id AS mpa_id,\n" +
            "    r.rating AS mpa_name,\n" +
            "    GROUP_CONCAT(CONCAT(g.genre_id, ' ', g.genre) ORDER BY g.genre_id SEPARATOR ', ') AS genre_list\n" +
            "FROM films AS f\n" +
            "JOIN ratings AS r ON f.rating_id = r.rating_id\n" +
            "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id\n" +
            "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id\n" +
            "WHERE f.film_id = ?\n" +
            "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id";
    public static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
    public static final String ADD_LIKE_QUERY = "INSERT INTO films_likes(film_id, user_id) VALUES(?, ?)";
    public static final String DELETE_LIKE_QUERY = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
    public static final String FIND_MOST_POPULAR_FILMS_QUERY = "SELECT\n" +
            "                f.film_id,\n" +
            "                f.name,\n" +
            "                f.description,\n" +
            "                f.release_date,\n" +
            "                f.duration,\n" +
            "                r.rating_id AS mpa_id,\n" +
            "                r.rating AS mpa_name,\n" +
            "                GROUP_CONCAT(CONCAT(g.genre_id, ' ', g.genre) ORDER BY g.genre_id SEPARATOR ', ') AS genre_list\n" +
            "            FROM films AS f\n" +
            "            JOIN ratings AS r ON f.rating_id = r.rating_id\n" +
            "            LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id\n" +
            "            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id\n" +
            "            JOIN (\n" +
            "               SELECT film_id,\n" +
            "                      COUNT(user_id) AS likes\n" +
            "                      FROM films_likes\n" +
            "                      GROUP BY film_id\n" +
            "                      ORDER BY likes \n" +
            "                      LIMIT ?) AS popular_films ON f.film_id = popular_films.film_id\n" +
            "            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id\n" +
            "            ORDER BY popular_films.likes DESC";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> filmRowMapper) {
        super(jdbc, filmRowMapper);
    }

    @Override
    public List<Film> findAllFilms() {
        return findAll(FIND_ALL_FILMS_QUERY);
    }

    public Optional<Film> findFilmById(long filmId) {
        return findById(FIND_FILM_BY_ID_QUERY, filmId);
    }

    @Override
    @Transactional
    public Film addFilm(Film film) {
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                processQuery(FilmDbStorage.INSERT_GENRES_QUERY, id, genre.getId());
            }
        }

        log.debug("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getId()
        );
        log.debug("Обновлен фильм {}", newFilm);
        return newFilm;
    }

    @Override
    public void addLike(long filmId, long userId) {
        processQuery(ADD_LIKE_QUERY, filmId, userId);
        log.debug("Фильму с id {} добавлен лайк от пользователя с {}", filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        processQuery(DELETE_LIKE_QUERY, filmId, userId);
        log.debug("У фильма с id {} удален лайк от пользователя с {}", filmId, userId);
    }

    @Override
    public List<Film> findMostPopularFilms(int count) {
        return findAll(FIND_MOST_POPULAR_FILMS_QUERY, count);
    }
}
