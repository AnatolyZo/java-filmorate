package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    List<MpaDto> findAllRatings();

    Optional<MpaDto> findMpaById(long mpaId);
}
