package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<GenreDto> implements GenreStorage {
    private static final String FIND_ALL_GENRES = "SELECT genre_id, genre FROM genres";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<GenreDto> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<GenreDto> findAllGenres() {
        return findAll(FIND_ALL_GENRES);
    }

    @Override
    public Optional<GenreDto> findGenreById(long genreId) {
        return findById(FIND_GENRE_BY_ID, genreId);
    }
}
