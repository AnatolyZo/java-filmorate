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
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

public class UsersPutTests {
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
    void testUpdateUser() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest updateToUser = UpdateUserRequest.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateToUser);

        for (ConstraintViolation<UpdateUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }
        UserDto updatedUser = userController.updateUser(updateToUser, bindingResult);

        Assertions.assertEquals(expectedUser, updatedUser);
    }

    @Test
    void testUpdateUserByUserWithIncorrectEmail() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest userWithWrongEmail1 = UpdateUserRequest.builder()
                .id(2L)
                .email("email2")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1996, 7, 2))
                .build();

        UpdateUserRequest userWithWrongEmail2 = UpdateUserRequest.builder()
                .id(3L)
                .email("@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        UpdateUserRequest userWithWrongEmail3 = UpdateUserRequest.builder()
                .id(4L)
                .email("email4@@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        UpdateUserRequest userWithWrongEmail4 = UpdateUserRequest.builder()
                .id(4L)
                .email("email4@")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1984, 11, 18))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(userWithWrongEmail1, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations1 = validator.validateValue(UpdateUserRequest.class,"email","email1");

        for (ConstraintViolation<UpdateUserRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail1, bindingResult1));

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(userWithWrongEmail2, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations2 = validator.validateValue(UpdateUserRequest.class,"email","email2@");

        for (ConstraintViolation<UpdateUserRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail2, bindingResult2));

        BeanPropertyBindingResult bindingResult3 = new BeanPropertyBindingResult(userWithWrongEmail3, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations3 = validator.validateValue(UpdateUserRequest.class,"email","@ya.ru");

        for (ConstraintViolation<UpdateUserRequest> violation : violations3) {
            bindingResult3.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(userWithWrongEmail3, bindingResult3));

        BeanPropertyBindingResult bindingResult4 = new BeanPropertyBindingResult(userWithWrongEmail4, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations4 = validator.validateValue(UpdateUserRequest.class,"email","email4@@ya.ru");

        for (ConstraintViolation<UpdateUserRequest> violation : violations4) {
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
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest updateToUser = UpdateUserRequest.builder()
                .id(1L)
                .email("email2@ya.ru")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validateValue(UpdateUserRequest.class,"login",null);

        for (ConstraintViolation<UpdateUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(updateToUser, bindingResult));
    }

    @Test
    void testUpdateUserByUserWithSpaceSignsInLogin() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest updateToUser1 = UpdateUserRequest.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("L ogin")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        UpdateUserRequest updateToUser2 = UpdateUserRequest.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("L og in")
                .name("Name2")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult1 = new BeanPropertyBindingResult(updateToUser1, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations1 = validator.validateValue(UpdateUserRequest.class,"login","L ogin");

        for (ConstraintViolation<UpdateUserRequest> violation : violations1) {
            bindingResult1.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        BeanPropertyBindingResult bindingResult2 = new BeanPropertyBindingResult(updateToUser2, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations2 = validator.validateValue(UpdateUserRequest.class,"login","L og in");

        for (ConstraintViolation<UpdateUserRequest> violation : violations2) {
            bindingResult2.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(updateToUser1, bindingResult1));
        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(updateToUser2, bindingResult2));
    }

    @Test
    void testUpdateUserByUserWithEmptyName() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest updateToUser = UpdateUserRequest.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1988, 11, 11))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateToUser);

        for (ConstraintViolation<UpdateUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto updatedUser = userController.updateUser(updateToUser, bindingResult);

        Assertions.assertEquals(expectedUser, updatedUser);
    }

    @Test
    void testUpdateUserByUserWithBirthdayDateToday() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest updateToUser = UpdateUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now())
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now())
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateToUser);

        for (ConstraintViolation<UpdateUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        UserDto updatedUser = userController.updateUser(updateToUser, bindingResult);

        Assertions.assertEquals(expectedUser, updatedUser);
    }

    @Test
    void testUpdateUserByUserWithBirthdayDateTomorrow() {
        NewUserRequest user = NewUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        UpdateUserRequest updateToUser = UpdateUserRequest.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        BeanPropertyBindingResult bindingResultUser = new BeanPropertyBindingResult(user, "user");
        Set<ConstraintViolation<NewUserRequest>> violationsUser = validator.validate(user);

        for (ConstraintViolation<NewUserRequest> violation : violationsUser) {
            bindingResultUser.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        userController.addUser(user, bindingResultUser);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(updateToUser, "user");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validateValue(UpdateUserRequest.class,"birthday",
                LocalDate.now().plusDays(1));

        for (ConstraintViolation<UpdateUserRequest> violation : violations) {
            bindingResult.rejectValue(
                    violation.getPropertyPath().toString(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
        }

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(updateToUser, bindingResult));
    }
}
