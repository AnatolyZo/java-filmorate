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
import java.util.Set;

public class UsersPutTests {
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
    void testUpdateUser() {
        User user = User.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateToUser = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(updateToUser);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        User updatedUser = userController.updateUser(updateToUser, bindingResult);

        Assertions.assertEquals(updateToUser, updatedUser);
    }

    @Test
    void testUpdateUserByUserWithIncorrectEmail() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User userWithWrongEmail1 = User.builder()
                .id(2L)
                .email("email2")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        User userWithWrongEmail2 = User.builder()
                .id(3L)
                .email("@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        User userWithWrongEmail3 = User.builder()
                .id(4L)
                .email("email4@@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        User userWithWrongEmail4 = User.builder()
                .id(4L)
                .email("email4@")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(userWithWrongEmail1, "user");
        Set<ConstraintViolation<User>> violations1 = validator.validateValue(User.class,"email","email1");

        for (ConstraintViolation<User> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail1, bindingResult1));

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(userWithWrongEmail2, "user");
        Set<ConstraintViolation<User>> violations2 = validator.validateValue(User.class,"email","email2@");

        for (ConstraintViolation<User> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail2, bindingResult2));

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(userWithWrongEmail3, "user");
        Set<ConstraintViolation<User>> violations3 = validator.validateValue(User.class,"email","@ya.ru");

        for (ConstraintViolation<User> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail3, bindingResult3));

        BeanPropertyBindingResult bindingResult4 = new BeanPropertyBindingResult(userWithWrongEmail4, "user");
        Set<ConstraintViolation<User>> violations4 = validator.validateValue(User.class,"email","email4@@ya.ru");

        for (ConstraintViolation<User> violation : violations4) {
            bindingResult4.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail4, bindingResult4));
    }

    @Test
    void testUpdateUserByUserWithEmptyLogin() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateToUser = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validateValue(User.class,"login",null);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(user, bindingResult));
    }

    @Test
    void testUpdateUserByUserWithSpaceSignsInLogin() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateToUser1 = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("L ogin")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        User updateToUser2 = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("L og in")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(updateToUser1, "user");
        Set<ConstraintViolation<User>> violations1 = validator.validateValue(User.class,"login","L ogin");

        for (ConstraintViolation<User> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(updateToUser2, "user");
        Set<ConstraintViolation<User>> violations2 = validator.validateValue(User.class,"login","L og in");

        for (ConstraintViolation<User> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(user, bindingResult1));
        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(user, bindingResult2));
    }

    @Test
    void testUpdateUserByUserWithEmptyName() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateToUser = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        User expectedUser = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login")
                .name("Login")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(updateToUser);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        User updatedUser = userController.updateUser(updateToUser, bindingResult);

        Assertions.assertEquals(expectedUser, updatedUser);
    }

    @Test
    void testUpdateUserByUserWithBirthdayDateToday() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateToUser = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now())
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(updateToUser);

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        User updatedUser = userController.updateUser(updateToUser, bindingResult);

        Assertions.assertEquals(updateToUser, updatedUser);
    }

    @Test
    void testUpdateUserByUserWithBirthdayDateTomorrow() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateToUser = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<User>> violationsUser = validator.validate(user);

        for (ConstraintViolation<User> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validateValue(User.class,"birthday",
                LocalDate.now().plusDays(1));

        for (ConstraintViolation<User> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(updateToUser, bindingResult));
    }
}
