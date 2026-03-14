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
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsersPostTests {
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
    void testAddUserWhen1UserAdded() {
        User user = User.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        User addedUser = userController.addUser(user, bindingResult);

        Assertions.assertEquals(user, addedUser);
    }

    @Test
    void testAddUserWhen3UsersAdded() {
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

        User addedUser1 = userController.addUser(user1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user2, "user");
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);

        for (ConstraintViolation<User> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        User addedUser2 = userController.addUser(user2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(user3, "user");
        Set<ConstraintViolation<User>> violations3 = validator.validate(user3);

        for (ConstraintViolation<User> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        User addedUser3 = userController.addUser(user3, bindingResult3);

        Assertions.assertEquals(user1, addedUser1);
        Assertions.assertEquals(user2, addedUser2);
        Assertions.assertEquals(user3, addedUser3);
        Assertions.assertIterableEquals(expectedMap.values(), inMemoryUserStorage.getUsers().values());
    }

    @Test
    void testAddUserWithIncorrectEmail() {
        User user1 = User.builder()
                .id(1L)
                .email("email1")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("email2@")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        User user3 = User.builder()
                .id(3L)
                .email("@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        User user4 = User.builder()
                .id(4L)
                .email("email4@@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<User>> violations1 = validator.validateValue(User.class,"email","email1");

        for (ConstraintViolation<User> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user1, bindingResult1));

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user2, "user");
        Set<ConstraintViolation<User>> violations2 = validator.validateValue(User.class,"email","email2@");

        for (ConstraintViolation<User> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user2, bindingResult2));

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(user3, "user");
        Set<ConstraintViolation<User>> violations3 = validator.validateValue(User.class,"email","@ya.ru");

        for (ConstraintViolation<User> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user3, bindingResult3));

        BeanPropertyBindingResult bindingResult4 = new BeanPropertyBindingResult(user4, "user");
        Set<ConstraintViolation<User>> violations4 = validator.validateValue(User.class,"email","email4@@ya.ru");

        for (ConstraintViolation<User> violation : violations4) {
            bindingResult4.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user4, bindingResult4));
    }

    @Test
    void testAddUserWithEmptyLogin() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violations = validator.validateValue(User.class,"login",null);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user, bindingResult));
    }

    @Test
    void testAddUserWithSpaceSignsInLogin() {
        User user1 = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("L ogin")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("L og in")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<User>> violations1 = validator.validateValue(User.class,"login","L ogin");

        for (ConstraintViolation<User> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<User>> violations2 = validator.validateValue(User.class,"login","L og in");

        for (ConstraintViolation<User> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user1, bindingResult1));
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user2, bindingResult2));
    }

    @Test
    void testAddUserWithEmptyName() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User expectedUser = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Login")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        User addedUser = userController.addUser(user, bindingResult);

        Assertions.assertEquals(expectedUser, addedUser);
    }

    @Test
    void testAddUserWithBirthdayDateToday() {
        User user = User.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now())
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        User addedUser = userController.addUser(user, bindingResult);

        Assertions.assertEquals(user, addedUser);
    }

    @Test
    void testAddUserWithBirthdayDateTomorrow() {
        User user = User.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violations = validator.validateValue(User.class,"birthday",
                LocalDate.now().plusDays(1));

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user, bindingResult));
    }
}
