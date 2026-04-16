package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {ConfigDataApplicationContextInitializer.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"classpath:schema-films.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Configuration
    @ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
    static class TestConfig {
        @Autowired
        private FilmRowMapper filmRowMapper;

        @Bean
        public FilmDbStorage filmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
            return new FilmDbStorage(jdbc, mapper);
        }
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 4");
        jdbcTemplate.execute("INSERT INTO films(film_id, name, description, release_date, duration, rating_id) VALUES\n" +
                "(1, 'Film1', 'Description1', '1990-01-01', 120, '1'),\n" +
                "(2, 'Film2', 'Description2', '1991-01-01', 130, '1'),\n" +
                "(3, 'Film3', 'Description3', '1992-01-01', 140, '1')");
        jdbcTemplate.execute("INSERT INTO films_genres(film_id, genre_id) VALUES\n" +
                "(1, 1),\n" +
                "(2, 1),\n" +
                "(3, 1)");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, login, name, birthday) VALUES\n" +
                "(1, 'user1@test.com', 'user1', 'User One', '1990-01-01'),\n" +
                "(2, 'user2@test.com', 'user2', 'User Two', '1995-05-15'),\n" +
                "(3, 'user3@test.com', 'user3', 'User Three', '2000-01-01'),\n" +
                "(4, 'user4@test.com', 'user4', 'User Four', '2001-01-01')");
    }

    @AfterEach
    void set() {
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films_likes");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void testFindFilmById() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        Film expectedFilm = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();


        Optional<Film> filmOptional = filmDbStorage.findFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(film).isEqualTo(expectedFilm);
                        }
                );
    }

    @Test
    public void testFindFilmByNotExistingId() {

        Optional<Film> filmOptional = filmDbStorage.findFilmById(500);

        assertThat(filmOptional)
                .isEmpty();
    }

    @Test
    public void testFindAllFilms() {
        Collection<Film> allFilms = filmDbStorage.findAllFilms();

        assertThat(allFilms).hasSize(3);
        assertThat(allFilms)
                .extracting("name")
                .contains("Film1", "Film2", "Film3");

    }

    @Test
    public void testAddFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        Film film1 = Film.builder()
                .id(8L)
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film film2 = Film.builder()
                .id(9L)
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1992, 1, 1))
                .duration(130)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film film3 = Film.builder()
                .id(10L)
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(1993, 1, 1))
                .duration(140)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film addedFilm1 = filmDbStorage.addFilm(film1);
        Film addedFilm2 = filmDbStorage.addFilm(film2);
        Film addedFilm3 = filmDbStorage.addFilm(film3);

        assertThat(addedFilm1).isEqualTo(film1);
        assertThat(addedFilm2).isEqualTo(film2);
        assertThat(addedFilm3).isEqualTo(film3);
    }

    @Test
    public void testUpdateFilm() {
        Film updateFilm1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(120)
                .build();

        Film updateFilm2 = Film.builder()
                .id(1L)
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1992, 1, 1))
                .duration(130)
                .build();

        Film updateFilm3 = Film.builder()
                .id(1L)
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(1993, 1, 1))
                .duration(140)
                .build();

        Film updatedFilm1 = filmDbStorage.updateFilm(updateFilm1);
        assertThat(updatedFilm1).isEqualTo(updateFilm1);
        Film updatedFilm2 = filmDbStorage.updateFilm(updateFilm2);
        assertThat(updatedFilm2).isEqualTo(updateFilm2);
        Film updatedFilm3 = filmDbStorage.updateFilm(updateFilm3);
        assertThat(updatedFilm3).isEqualTo(updateFilm3);
    }

    @Test
    public void testAddLike() {
        List<Film> popularFilms = filmDbStorage.findMostPopularFilms(1);
        assertThat(popularFilms).isEmpty();

        filmDbStorage.addLike(1, 2);

        List<Film> popularFilmsAfterAddingLike = filmDbStorage.findMostPopularFilms(1);
        assertThat(popularFilmsAfterAddingLike).hasSize(1);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        Film expectedFilm = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();

        assertThat(popularFilmsAfterAddingLike).contains(expectedFilm);
    }

    @Test
    public void testDeleteLike() {
        filmDbStorage.addLike(1, 2);
        List<Film> popularFilms = filmDbStorage.findMostPopularFilms(1);
        assertThat(popularFilms).hasSize(1);

        filmDbStorage.deleteLike(1,2);

        List<Film> popularFilmsAfterDeleteLike = filmDbStorage.findMostPopularFilms(1);
        assertThat(popularFilmsAfterDeleteLike).isEmpty();
    }

    @Test
    public void testFindMostPopularFilms() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1991, 1, 1))
                .duration(130)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film film3 = Film.builder()
                .id(3L)
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(1992, 1, 1))
                .duration(140)
                .mpa(mpa)
                .genres(genres)
                .build();

        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);

        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(2, 2);
        filmDbStorage.addLike(2, 3);
        filmDbStorage.addLike(2, 4);

        filmDbStorage.addLike(3, 1);
        filmDbStorage.addLike(3, 2);
        filmDbStorage.addLike(3, 3);

        List<Film> popularFilms = filmDbStorage.findMostPopularFilms(3);

        assertThat(popularFilms).hasSize(3);
        assertThat(popularFilms).containsExactly(film2, film3, film1);
    }
}
