package ru.yandex.practicum.filmorate.validation.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

//Валидация id пользователя или фильма в зависимости от типа хранилища
@Slf4j
public class IdValidator {
    public static void validate(Object storage, long id) {
        if (storage instanceof InMemoryFilmStorage inMemoryFilmStorage) {
            if (inMemoryFilmStorage.getFilm(id) == null) {
                log.info("Фильм c id {} не найден", id);
                throw new NotFoundException(String.format("Фильм c id %d не найден", id));
            }
        } else if (storage instanceof InMemoryUserStorage inMemoryUserStorage) {
            if (inMemoryUserStorage.getUser(id) == null) {
                log.info("Пользователь с id {} не найден", id);
                throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
            }
        } else {
            log.info("Неподдерживаемый тип хранилища");
            throw new IllegalArgumentException("Неподдерживаемый тип хранилища");
        }
    }
}
