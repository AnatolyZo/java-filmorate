package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {ConfigDataApplicationContextInitializer.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTests {
    private final GenreDbStorage genreDbStorage;

    @Configuration
    @ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
    static class TestConfig {
        @Autowired
        private GenreRowMapper genreRowMapper;

        @Bean
        public GenreDbStorage genreDbStorage(JdbcTemplate jdbc, RowMapper<GenreDto> mapper) {
            return new GenreDbStorage(jdbc, mapper);
        }
    }

    @Test
    public void testFindGenreById() {
        Optional<GenreDto> genreOptional = genreDbStorage.findGenreById(1);

        GenreDto expectedGenre = GenreDto.builder()
                .id(1L)
                .name("Комедия")
                .build();

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                            assertThat(genre).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(genre).isEqualTo(expectedGenre);
                        }

                );
    }

    @Test
    public void testFindGenreByNotExistingId() {
        Optional<GenreDto> genreOptional = genreDbStorage.findGenreById(500);

        assertThat(genreOptional)
                .isEmpty();
    }

    @Test
    public void testFindAllGenres() {
        Collection<GenreDto> allGenres = genreDbStorage.findAllGenres();

        assertThat(allGenres).hasSize(6);
        assertThat(allGenres)
                .extracting("name")
                .contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");

    }
}
