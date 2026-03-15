package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ValidationResults {
    //Метод выводит все ошибки в запросе
    public static void extract(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> validationFails = bindingResult.getFieldErrors().stream()
                    .map(error -> "Поле: " + error.getField() + ", ошибка: " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            log.info("Обнаружены следующие ошибки валидации:\n{}", String.join("\n", validationFails));
            throw new ValidationException("Обнаружены следующие ошибки валидации:\n" + String.join("\n", validationFails));
        }
    }
}
