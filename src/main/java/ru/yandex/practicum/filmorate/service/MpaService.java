package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<MpaDto> findAllRatings() {
        return mpaStorage.findAllRatings();
    }

    public MpaDto findMpaById(long mpaId) {
        return mpaStorage.findMpaById(mpaId).orElseThrow(() -> new NotFoundException(String.format("Рейтинг с ID %d не найден", mpaId)));
    }
}
