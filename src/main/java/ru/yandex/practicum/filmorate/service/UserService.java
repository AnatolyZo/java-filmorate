package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(long userId, long friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.setNewFriend(friendId);
        friend.setNewFriend(userId);
        log.debug("Пользоатель с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        log.debug("Пользоатель с id {} удалил из друзей пользователя с id {}", userId, friendId);
        return friend;
    }

    public List<User> showFriends(long userId) {
        userStorage.validateId(userId);
        User user = userStorage.getUser(userId);

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .toList();
    }

    public List<User> showCommonFriends(long userId, long otherId) {
        userStorage.validateId(userId);
        userStorage.validateId(otherId);
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherId);

        Set<Long> usersFriends = user.getFriends();
        Set<Long> otherUsersFriends = otherUser.getFriends();
        return usersFriends.stream()
                .filter(otherUsersFriends::contains)
                .map(userStorage::getUser)
                .toList();
    }
}
