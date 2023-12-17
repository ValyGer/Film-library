package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private FilmService filmService;

    @PostMapping // Добавление нового фильма
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping // Обновление информации о фильме или создание нового
    public Film loadFilm(@RequestBody Film film) {
        return filmService.loadFilm(film);
    }

    @GetMapping // Возвращает весь список фильмов
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }
}
