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

    @GetMapping("/{id}") // Возвращает фильм по id
    public Film getFilmById(@PathVariable("id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}") // Добавление лайка к фильму пользователем
    public Film addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}") // Удаление лайка у фильма пользователем
    public Film deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular") // Получение списка популярных фильмов
    public List<Film> findPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) String count) {
        return filmService.findPopularFilms(count);
    }

}
