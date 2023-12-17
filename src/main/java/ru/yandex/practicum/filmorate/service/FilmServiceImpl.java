package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private InMemoryFilmStorage inMemoryFilmStorage;

    public Film createFilm(Film film) {
        validateFilm(film);
        log.debug("Добавлен новый фильм {}", film);
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film loadFilm(Film film) {
        validateFilm(film);
        log.debug("Фильм {} обновлен", film);
        return inMemoryFilmStorage.loadFilm(film);
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }
}
