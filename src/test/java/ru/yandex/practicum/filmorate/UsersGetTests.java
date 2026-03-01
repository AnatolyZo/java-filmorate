package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class UsersGetTests {
    @Autowired
    private UserController userController;

    @AfterEach
    void afterEach() {
        userController.getUsers().clear();
    }

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testFindAllUsersWhenNoUsersAdded() {
        Collection<User> users = userController.findAllUsers();

        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testFindAllUsersWhen3UsersAdded() {
        User user1 = User.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        User user3 = User.builder()
                .id(3L)
                .email("email3@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        Map<Long, User> expectedMap = new HashMap<>();
        expectedMap.put(user1.getId(), user1);
        expectedMap.put(user2.getId(), user2);
        expectedMap.put(user3.getId(), user3);

        userController.getUsers().put(user1.getId(), user1);
        userController.getUsers().put(user2.getId(), user2);
        userController.getUsers().put(user3.getId(), user3);

        Collection<User> users = userController.findAllUsers();

        Assertions.assertIterableEquals(expectedMap.values(), users);
    }
}
