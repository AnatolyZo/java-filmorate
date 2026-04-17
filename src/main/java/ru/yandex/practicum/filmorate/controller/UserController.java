package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody NewUserRequest user, BindingResult bindingResult) {
        return userService.addUser(user, bindingResult);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest user, BindingResult bindingResult) {
        return userService.updateUser(user, bindingResult);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable(name = "id") long userId, @PathVariable long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> showFriends(@PathVariable(name = "id") long userId) {
        return userService.showFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> showCommonFriends(@PathVariable(name = "id") long userId, @PathVariable long otherId) {
        return userService.showCommonFriends(userId, otherId);
    }
}
