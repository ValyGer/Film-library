package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("checkstyle:Regexp")
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final  Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static int generateFilmId = 0;
    private final FilmValidation filmValidation = new FilmValidation();

    @PostMapping // Добавление нового фильма
    public Film createFilm(@RequestBody Film film) {
        filmValidation.validateFilm(film);
        film.setId(++generateFilmId);
        films.put(film.getId(), film);
        log.debug("Добавлен новый фильм {}", film);
        return film;
    }

    @PutMapping // Обновление информации о фильме или создание нового
    public Film loadFilm(@RequestBody Film film) {
        filmValidation.validateFilm(film);
        Film saved = films.get(film.getId());
        if (saved == null) {
            log.debug("Фильм не найден {}", film);
            throw new ValidationException("Фильм не найден");
        } else {
            films.put(saved.getId(), film);
            log.debug("Фильм {} обновлен", film);
        }
        return film;
    }

    @GetMapping // Возвращает весь список фильмов
        public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
