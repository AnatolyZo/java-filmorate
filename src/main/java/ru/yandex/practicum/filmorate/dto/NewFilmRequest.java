package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.annotations.IsCorrectGenre;
import ru.yandex.practicum.filmorate.validation.annotations.IsCorrectMPARating;
import ru.yandex.practicum.filmorate.validation.annotations.IsDateAfter;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class NewFilmRequest {
    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    @IsDateAfter
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private int duration;
    @IsCorrectGenre
    private List<Genre> genres;
    @IsCorrectMPARating
    private Mpa mpa;
}
