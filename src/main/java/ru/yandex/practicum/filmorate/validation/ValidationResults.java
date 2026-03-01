package ru.yandex.practicum.filmorate.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationResults {
    private static final Logger log = LoggerFactory.getLogger(ValidationResults.class);

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
