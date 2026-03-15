package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        return filmStorage.addFilm(film, bindingResult);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm, BindingResult bindingResult) {
        return filmStorage.updateFilm(newFilm, bindingResult);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable(name = "id") long filmId, @PathVariable long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable(name = "id") long filmId, @PathVariable long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> showPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
