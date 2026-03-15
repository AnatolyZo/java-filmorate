package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationResults;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        user.setId(getNextId());
        log.debug("Новому пользователю присвоен id {}", user.getId());
        fillNameIfEmpty(user);

        try {
            users.put(user.getId(), user);
            log.debug("Добавлен новый пользователь {}", user.getName());
        } catch (RuntimeException e) {
            log.error("Ошибка добавления нового пользователя", e);
            throw new RuntimeException("Ошибка добавления нового пользователя");
        }

        return user;
    }

    @Override
    public User updateUser(User newUser, BindingResult bindingResult) {
        if (newUser.getId() == null) {
            log.info("Не передан id пользователя");
            throw new RuntimeException("ID не указан, невозможно извлечь данные");
        }

        ValidationResults.extract(bindingResult);

        if (users.containsKey(newUser.getId())) {
            fillNameIfEmpty(newUser);
            User updatingUser = users.get(newUser.getId());

            try {
                updateFields(updatingUser, newUser);
                log.debug("Обновлены данные пользователя {} c id {}", newUser, newUser.getId());
            } catch (RuntimeException e) {
                log.error("Ошибка обновления данных пользователя", e);
                throw new RuntimeException("Ошибка обновления данных пользователя");
            }

            return updatingUser;
        }

        log.info("Передан несуществующий id пользователя {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User getUser(long userId) {
        return users.get(userId);
    }

    @Override
    public void validateId(long userId) {
        if (users.get(userId) == null) {
            log.info("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }

    //Метод по обновлению полей
    private void updateFields(User updatingUser, User newUser) {
        updatingUser.setEmail(newUser.getEmail());
        log.debug("У пользователя c id {} обновлен email на {}", updatingUser.getId(), newUser.getEmail());
        updatingUser.setLogin(newUser.getLogin());
        log.debug("У пользователя c id {} обновлен логин на {}", updatingUser.getId(), newUser.getLogin());
        updatingUser.setName(newUser.getName());
        log.debug("У пользователя c id {} обновлено имя на {}", updatingUser.getId(), newUser.getName());
        updatingUser.setBirthday(newUser.getBirthday());
        log.debug("У пользователя c id {} обновлена дата рождения на {}", updatingUser.getId(), newUser.getBirthday());
    }

    //Метод генерации id
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    //Метод по заполнению имени, если в запросе оно не указано
    private void fillNameIfEmpty(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("У пользователя с id {} заполнено пустое имя на {}", user.getName(), user.getLogin());
        }
    }
}
