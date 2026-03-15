package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsersGetTests {
    private UserController userController;
    private InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(inMemoryUserStorage);
    private Validator validator;

    @AfterEach
    void afterEach() {
        inMemoryUserStorage.getUsers().clear();
    }

    @BeforeEach
    void setUp() {
        userController = new UserController(inMemoryUserStorage, userService);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);

        for (ConstraintViolation<User> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user2, "user");
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);

        for (ConstraintViolation<User> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(user3, "user");
        Set<ConstraintViolation<User>> violations3 = validator.validate(user3);

        for (ConstraintViolation<User> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        userController.addUser(user3, bindingResult3);

        Collection<User> users = userController.findAllUsers();

        Assertions.assertIterableEquals(expectedMap.values(), users);
    }
}
