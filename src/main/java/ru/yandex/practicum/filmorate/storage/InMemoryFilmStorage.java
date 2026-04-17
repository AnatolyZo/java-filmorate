package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

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
    public Optional<Film> findFilmById(long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Film addFilm(Film film) {

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
    public void addLike(long filmId, long userId) {
        Film film = films.get(filmId);

        film.setNewLike(userId);
        log.debug("Фильму с id {} добавлен лайк от пользователя с id {}", filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = films.get(filmId);

        film.deleteLike(userId);
        log.debug("У фильма с id {} удален лайк от пользователя с id {}", filmId, userId);
    }

    @Override
    public Film updateFilm(Film newFilm) {

        if (newFilm.getId() == null) {
            log.warn("Не передан id объекта фильма {}", newFilm);
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
    public List<Film> findMostPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::countLikes).reversed())
                .limit(count)
                .toList();
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
