package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UsersFriends;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final List<UsersFriends> usersFriends = new ArrayList<>();

    public User addFriend(long userId, long friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);

        //Находим пользователя, который подавал заявку в друзья ранее
        Optional<UsersFriends> potentialFriendPair = usersFriends.stream()
                .filter(pair -> pair.getUserId() == friendId && pair.getFriendId() == userId)
                .findFirst();

        //В случае если пользователь с friendId подавал заявку, то добавляем в друзья и обновляем статус подтверждения
        if (potentialFriendPair.isPresent()) {
            usersFriends.add(new UsersFriends(userId, friendId, true));
            potentialFriendPair.get().setConfirmedFriend(true);
        } else {
            usersFriends.add(new UsersFriends(userId, friendId, false));
        }

        log.debug("Пользоатель с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return userStorage.getUser(userId);
    }

    public User deleteFriend(long userId, long friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);

        //Если была подтверждена дружба с другим пользователем, то убираем подтверждение
        usersFriends.stream()
                .filter(pair -> pair.getUserId() == friendId && pair.getFriendId() == userId)
                .findFirst()
                .ifPresent(friend -> friend.setConfirmedFriend(false));

        //Удаляем друга из списка
        usersFriends.stream()
                .filter(pair -> pair.getUserId() == userId && pair.getFriendId() == friendId)
                .findFirst()
                .ifPresent(usersFriends::remove);

        log.debug("Пользоатель с id {} удалил из друзей пользователя с id {}", userId, friendId);
        return userStorage.getUser(friendId);
    }

    public List<User> showFriends(long userId) {
        userStorage.validateId(userId);

        return usersFriends.stream()
                .filter(pair -> pair.getUserId() == userId && pair.isConfirmedFriend())
                .map(pair -> userStorage.getUser(pair.getFriendId()))
                .toList();
    }

    public List<User> showCommonFriends(long userId, long otherId) {
        userStorage.validateId(userId);
        userStorage.validateId(otherId);

        //Получаем список подтвержденных друзей у пользователя
        List<Long> usersFriendsList = usersFriends.stream()
                .filter(pair -> pair.getUserId() == userId && pair.isConfirmedFriend())
                .map(UsersFriends::getFriendId)
                .toList();

        //Получаем список подтвержденных друзей у другого пользователя
        List<Long> otherUsersFriends = usersFriends.stream()
                .filter(pair -> pair.getUserId() == otherId && pair.isConfirmedFriend())
                .map(UsersFriends::getFriendId)
                .toList();

        //Выводим список общих подтвержденных друзей
        return usersFriendsList.stream()
                .filter(otherUsersFriends::contains)
                .map(userStorage::getUser)
                .toList();
    }
}
