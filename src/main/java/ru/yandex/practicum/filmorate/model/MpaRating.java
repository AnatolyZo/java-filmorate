package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

public enum MpaRating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String russianName;

    MpaRating(String russianName) {
        this.russianName = russianName;
    }

    public static String getDescription(long index) {
        for (MpaRating mpaRating : MpaRating.values()) {
            if (index == mpaRating.ordinal() + 1) {
                return mpaRating.russianName;
            }
        }
        throw new NotFoundException(String.format("Жанр с ID %d не найден", index));
    }
}
