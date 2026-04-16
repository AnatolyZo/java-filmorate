package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<GenreDto> {
    @Override
    public GenreDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return GenreDto.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("genre"))
                .build();
    }
}
