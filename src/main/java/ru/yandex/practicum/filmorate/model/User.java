package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotations.IsContainsSpaceSigns;
import ru.yandex.practicum.filmorate.validation.annotations.IsDateBefore;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Long id;
    @NotNull
    @Email
    private String email;
    @NotBlank
    @IsContainsSpaceSigns
    private String login;
    private String name;
    @IsDateBefore
    private LocalDate birthday;
}
