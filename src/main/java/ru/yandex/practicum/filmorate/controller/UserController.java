package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationResults;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Getter
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        user.setId(getNextId());
        fillNameIfEmpty(user);

        try {
            users.put(user.getId(), user);
        } catch (RuntimeException e) {
            log.error("Ошибка добавления нового пользователя", e);
            throw new RuntimeException("Ошибка добавления нового пользователя");
        }

        log.debug("Пользователю {} присвоен id {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser, BindingResult bindingResult) {
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
            } catch (RuntimeException e) {
                log.error("Ошибка обновления данных пользователя", e);
                throw new RuntimeException("Ошибка обновления данных пользователя");
            }

            log.debug("Обновлены данные пользователя {} c id {}", newUser, newUser.getId());
            return updatingUser;
        }

        log.info("Передан несуществующий id пользователя {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    //Метод по обновлению полей
    private void updateFields(User updatingUser, User newUser) {
        updatingUser.setEmail(newUser.getEmail());
        updatingUser.setLogin(newUser.getLogin());
        updatingUser.setName(newUser.getName());
        updatingUser.setBirthday(newUser.getBirthday());
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
        }
    }
}
