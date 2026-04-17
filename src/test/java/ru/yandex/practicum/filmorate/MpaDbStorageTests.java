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
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(initializers = {ConfigDataApplicationContextInitializer.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTests {
    private final MpaDbStorage mpaDbStorage;

    @Configuration
    @ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
    static class TestConfig {
        @Autowired
        private MpaRatingRowMapper mpaRatingRowMapper;

        @Bean
        public MpaDbStorage mpaDbStorage(JdbcTemplate jdbc, RowMapper<MpaDto> mapper) {
            return new MpaDbStorage(jdbc, mapper);
        }
    }

    @Test
    public void testFindMpaById() {
        Optional<MpaDto> mpaOptional = mpaDbStorage.findMpaById(1);

        MpaDto expectedMpa = MpaDto.builder()
                .id(1L)
                .name("G")
                .build();

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                            assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(mpa).isEqualTo(expectedMpa);
                        }

                );
    }

    @Test
    public void testFindMpaByNotExistingId() {
        Optional<MpaDto> mpaOptional = mpaDbStorage.findMpaById(500);

        assertThat(mpaOptional)
                .isEmpty();
    }

    @Test
    public void testFindAllMpa() {
        Collection<MpaDto> allMpa = mpaDbStorage.findAllRatings();

        assertThat(allMpa).hasSize(5);
        assertThat(allMpa)
                .extracting("name")
                .contains("G", "PG", "PG-13", "R", "NC-17");

    }
}
