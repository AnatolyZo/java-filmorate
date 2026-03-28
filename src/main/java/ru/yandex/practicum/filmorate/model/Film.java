package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.annotations.IsDateAfter;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    @IsDateAfter
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private int duration;
    private MPARating mpaRating;
}