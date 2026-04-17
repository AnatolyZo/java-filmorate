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
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsersGetTests {
    private UserController userController;
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(inMemoryUserStorage);
    private Validator validator;

    @AfterEach
    void afterEach() {
        inMemoryUserStorage.getUsers().clear();
    }

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFindAllUsersWhenNoUsersAdded() {
        Collection<UserDto> users = userController.findAllUsers();

        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testFindAllUsersWhen3UsersAdded() {
        NewUserRequest user1 = NewUserRequest.builder()
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        NewUserRequest user2 = NewUserRequest.builder()
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        NewUserRequest user3 = NewUserRequest.builder()
                .email("email3@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        UserDto user1Dto = UserDto.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UserDto user2Dto = UserDto.builder()
                .id(2L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        UserDto user3Dto = UserDto.builder()
                .id(3L)
                .email("email3@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        Map<Long, UserDto> expectedMap = new HashMap<>();
        expectedMap.put(user1Dto.getId(), user1Dto);
        expectedMap.put(user2Dto.getId(), user2Dto);
        expectedMap.put(user3Dto.getId(), user3Dto);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<NewUserRequest>> violations1 = validator.validate(user1);

        for (ConstraintViolation<NewUserRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user2, "user");
        Set<ConstraintViolation<NewUserRequest>> violations2 = validator.validate(user2);

        for (ConstraintViolation<NewUserRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(user3, "user");
        Set<ConstraintViolation<NewUserRequest>> violations3 = validator.validate(user3);

        for (ConstraintViolation<NewUserRequest> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        userController.addUser(user3, bindingResult3);

        Collection<UserDto> users = userController.findAllUsers();

        Assertions.assertIterableEquals(expectedMap.values(), users);
    }
}
