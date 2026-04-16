package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<MpaDto> implements MpaStorage {
    private static final String FIND_ALL_MPA = "SELECT * FROM ratings";
    private static final String FIND_MPA_BY_ID = "SELECT * FROM ratings WHERE rating_id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<MpaDto> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<MpaDto> findAllRatings() {
        return findAll(FIND_ALL_MPA);
    }

    @Override
    public Optional<MpaDto> findMpaById(long mpaId) {
        return findById(FIND_MPA_BY_ID, mpaId);
    }
}
