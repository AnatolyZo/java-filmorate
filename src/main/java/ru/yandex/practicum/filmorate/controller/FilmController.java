package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ValidationResults;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    @Getter
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        film.setId(getNextId());

        try {
            films.put(film.getId(), film);
        } catch (RuntimeException e) {
            log.error("Ошибка добавления нового фильма", e);
            throw new RuntimeException("Ошибка добавления нового фильма");
        }

        log.debug("Фильму {} присвоен id {}", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        if (newFilm.getId() == null) {
            log.info("Не передан id фильма");
            throw new RuntimeException("ID не указан, невозможно извлечь данные");
        }

        if (films.containsKey(newFilm.getId())) {
            Film updatingFilm = films.get(newFilm.getId());

            try {
                updateFields(updatingFilm, newFilm);
            } catch (RuntimeException e) {
                log.error("Ошибка обновления данных фильма", e);
                throw new RuntimeException("Ошибка обновления данных фильма");
            }

            log.debug("Обновлены данные фильма {} c id {}", newFilm, newFilm.getId());
            return updatingFilm;
        }

        log.info("Передан несуществующий id фильма {}", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    //Метод по обновлению полей
    private void updateFields(Film updatingFilm, Film newFilm) {
        updatingFilm.setName(newFilm.getName());
        updatingFilm.setDescription(newFilm.getDescription());
        updatingFilm.setReleaseDate(newFilm.getReleaseDate());
        updatingFilm.setDuration(newFilm.getDuration());
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
