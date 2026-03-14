package ru.yandex.practicum.filmorate.storage;

import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();

    User addUser(User user, BindingResult bindingResult);

    User updateUser(User newUser, BindingResult bindingResult);
}
