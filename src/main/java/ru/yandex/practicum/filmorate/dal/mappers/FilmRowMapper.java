package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        String genresRow = resultSet.getString("genre_list");
        //Для возврата фильма требуется сопоставить id с названием и добавить жанр
        List<Genre> genres = Arrays.stream(genresRow.split(", "))
                //Если список жанров пуст, то строка будет содержать только пробелы, этот случай надо обработать
                .filter(pairs -> !pairs.isBlank())
                .map(pairs -> pairs.split(" "))
                .map(pair -> {
                        Genre genre = new Genre();
                        genre.setId(Long.parseLong(pair[0]));
                        genre.setName(pair[1]);
                        return genre;
                })
                .toList();

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("rating_id"));
        mpa.setName(resultSet.getString("rating"));

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(releaseDate.toLocalDateTime().toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }
}
