package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationResults;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto addUser(NewUserRequest request, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        User user = UserMapper.mapToUser(request);
        user = userStorage.addUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request, BindingResult bindingResult) {
        ValidationResults.extract(bindingResult);

        User updatedUser = userStorage.findUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %d не найден", request.getId())));

        updatedUser = userStorage.updateUser(updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    public void addFriend(long userId, long friendId) {

        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<UserDto> showFriends(long userId) {
        return userStorage.getUsersFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> showCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
