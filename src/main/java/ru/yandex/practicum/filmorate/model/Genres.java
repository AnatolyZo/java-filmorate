package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Getter
public enum Genres {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String russianName;

    Genres(String russianName) {
        this.russianName = russianName;
    }

    public static String getDescription(long index) {
        for (Genres genre : Genres.values()) {
            if (index == genre.ordinal() + 1) {
                return genre.russianName;
            }
        }
        throw new NotFoundException(String.format("Жанр с ID %d не найден", index));
    }
}
