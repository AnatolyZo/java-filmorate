package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<GenreDto> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public GenreDto findGenreById(long genreId) {
        return genreStorage.findGenreById(genreId).orElseThrow(() -> new NotFoundException(String.format("Жанр с ID %d не найден", genreId)));
    }
}
