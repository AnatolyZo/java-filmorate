package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.validators.IdValidator;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(long userId, long friendId) {
        IdValidator.validate(inMemoryUserStorage, userId);
        IdValidator.validate(inMemoryUserStorage, friendId);
        User user = inMemoryUserStorage.getUser(userId);
        User friend = inMemoryUserStorage.getUser(friendId);

        user.setNewFriend(friendId);
        friend.setNewFriend(userId);
        log.debug("Пользоатель с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        IdValidator.validate(inMemoryUserStorage, userId);
        IdValidator.validate(inMemoryUserStorage, friendId);
        User user = inMemoryUserStorage.getUser(userId);
        User friend = inMemoryUserStorage.getUser(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        log.debug("Пользоатель с id {} удалил из друзей пользователя с id {}", userId, friendId);
        return friend;
    }

    public List<User> showFriends(long userId) {
        IdValidator.validate(inMemoryUserStorage, userId);
        User user = inMemoryUserStorage.getUser(userId);

        return user.getFriends().stream()
                .map(inMemoryUserStorage::getUser)
                .toList();
    }

    public List<User> showCommonFriends(long userId, long otherId) {
        IdValidator.validate(inMemoryUserStorage, userId);
        IdValidator.validate(inMemoryUserStorage, otherId);
        User user = inMemoryUserStorage.getUser(userId);
        User otherUser = inMemoryUserStorage.getUser(otherId);

        Set<Long> usersFriends = user.getFriends();
        Set<Long> otherUsersFriends = otherUser.getFriends();
        return usersFriends.stream()
                .filter(otherUsersFriends::contains)
                .map(inMemoryUserStorage::getUser)
                .toList();
    }
}
