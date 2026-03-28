package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ValidationResults;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        film.setId(getNextId());
        log.debug("Новому фильму присвоен id {}", film.getId());

        try {
            films.put(film.getId(), film);
            log.debug("Добавлен новый фильм с id {}", film.getId());
        } catch (RuntimeException e) {
            log.error("Ошибка добавления нового фильма", e);
            throw new RuntimeException("Ошибка добавления нового фильма");
        }

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        if (newFilm.getId() == null) {
            log.info("Не передан id фильма");
            throw new RuntimeException("ID не указан, невозможно извлечь данные");
        }

        if (films.containsKey(newFilm.getId())) {
            Film updatingFilm = films.get(newFilm.getId());

            try {
                updateFields(updatingFilm, newFilm);
                log.debug("Обновлены данные фильма {} c id {}", newFilm, newFilm.getId());
            } catch (RuntimeException e) {
                log.error("Ошибка обновления данных фильма", e);
                throw new RuntimeException("Ошибка обновления данных фильма");
            }

            return updatingFilm;
        }

        log.info("Передан несуществующий id фильма {}", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film getFilm(long filmId) {
        return films.get(filmId);
    }

    @Override
    public void validateId(long filmId) {
        if (films.get(filmId) == null) {
            log.info("Фильм c id {} не найден", filmId);
            throw new NotFoundException(String.format("Фильм c id %d не найден", filmId));
        }
    }

    //Метод по обновлению полей
    private void updateFields(Film updatingFilm, Film newFilm) {
        updatingFilm.setName(newFilm.getName());
        log.debug("У фильма c id {} обновлено название на {}", updatingFilm.getId(), newFilm.getName());
        updatingFilm.setDescription(newFilm.getDescription());
        log.debug("У фильма c id {} обновлено описание на {}", updatingFilm.getId(), newFilm.getDescription());
        updatingFilm.setReleaseDate(newFilm.getReleaseDate());
        log.debug("У фильма c id {} обновлена дата выхода фильма на {}", updatingFilm.getId(), newFilm.getReleaseDate());
        updatingFilm.setDuration(newFilm.getDuration());
        log.debug("У фильма c id {} обновлена продолжительность на {}", updatingFilm.getId(), newFilm.getDuration());
    }

    //Метод генерации id
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
