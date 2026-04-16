package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingRowMapper implements RowMapper<MpaDto> {
    @Override
    public MpaDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaDto.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("rating"))
                .build();
    }
}
