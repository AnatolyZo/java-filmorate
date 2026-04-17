package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAllUsers();

    Optional<User> findUserById(long userId);

    User addUser(User user);

    User updateUser(User newUser);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getUsersFriends(long friendId);

    List<User> getCommonFriends(long userId, long anotherUserId);
}
