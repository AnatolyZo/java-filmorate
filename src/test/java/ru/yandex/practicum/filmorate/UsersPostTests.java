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
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

public class UsersPostTests {
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
    void testAddUserWhen1UserAdded() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto addedUser = userController.addUser(user, bindingResult);

        Assertions.assertEquals(userDto, addedUser);
    }

    @Test
    void testAddUserWhen3UsersAdded() {
        NewUserRequest user1 = NewUserRequest.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        NewUserRequest user2 = NewUserRequest.builder()
                .id(2L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        NewUserRequest user3 = NewUserRequest.builder()
                .id(3L)
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

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<NewUserRequest>> violations1 = validator.validate(user1);

        for (ConstraintViolation<NewUserRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto addedUser1 = userController.addUser(user1, bindingResult1);

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user2, "user");
        Set<ConstraintViolation<NewUserRequest>> violations2 = validator.validate(user2);

        for (ConstraintViolation<NewUserRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto addedUser2 = userController.addUser(user2, bindingResult2);

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(user3, "user");
        Set<ConstraintViolation<NewUserRequest>> violations3 = validator.validate(user3);

        for (ConstraintViolation<NewUserRequest> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        UserDto addedUser3 = userController.addUser(user3, bindingResult3);

        Assertions.assertEquals(user1Dto, addedUser1);
        Assertions.assertEquals(user2Dto, addedUser2);
        Assertions.assertEquals(user3Dto, addedUser3);
    }

    @Test
    void testAddUserWithIncorrectEmail() {
        NewUserRequest user1 = NewUserRequest.builder()
                .id(1L)
                .email("email1")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        NewUserRequest user2 = NewUserRequest.builder()
                .id(2L)
                .email("email2@")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        NewUserRequest user3 = NewUserRequest.builder()
                .id(3L)
                .email("@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        NewUserRequest user4 = NewUserRequest.builder()
                .id(4L)
                .email("email4@@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<NewUserRequest>> violations1 = validator.validateValue(NewUserRequest.class,"email","email1");

        for (ConstraintViolation<NewUserRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user1, bindingResult1));

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user2, "user");
        Set<ConstraintViolation<NewUserRequest>> violations2 = validator.validateValue(NewUserRequest.class,"email","email2@");

        for (ConstraintViolation<NewUserRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user2, bindingResult2));

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(user3, "user");
        Set<ConstraintViolation<NewUserRequest>> violations3 = validator.validateValue(NewUserRequest.class,"email","@ya.ru");

        for (ConstraintViolation<NewUserRequest> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user3, bindingResult3));

        BeanPropertyBindingResult bindingResult4 = new BeanPropertyBindingResult(user4, "user");
        Set<ConstraintViolation<NewUserRequest>> violations4 = validator.validateValue(NewUserRequest.class,"email","email4@@ya.ru");

        for (ConstraintViolation<NewUserRequest> violation : violations4) {
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
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateValue(NewUserRequest.class,"login",null);

        for (ConstraintViolation<NewUserRequest> violation : violations) {
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
        NewUserRequest user1 = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("L ogin")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        NewUserRequest user2 = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("L og in")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<NewUserRequest>> violations1 = validator.validateValue(NewUserRequest.class,"login","L ogin");

        for (ConstraintViolation<NewUserRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(user1, "user");
        Set<ConstraintViolation<NewUserRequest>> violations2 = validator.validateValue(NewUserRequest.class,"login","L og in");

        for (ConstraintViolation<NewUserRequest> violation : violations2) {
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
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Login")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto addedUser = userController.addUser(user, bindingResult);

        Assertions.assertEquals(expectedUser, addedUser);
    }

    @Test
    void testAddUserWithBirthdayDateToday() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now())
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now())
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto addedUser = userController.addUser(user, bindingResult);

        Assertions.assertEquals(userDto, addedUser);
    }

    @Test
    void testAddUserWithBirthdayDateTomorrow() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateValue(NewUserRequest.class,"birthday",
                LocalDate.now().plusDays(1));

        for (ConstraintViolation<NewUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user, bindingResult));
    }
}
