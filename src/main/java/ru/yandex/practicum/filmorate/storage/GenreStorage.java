package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<GenreDto> findAllGenres();

    Optional<GenreDto> findGenreById(long genreId);
}
