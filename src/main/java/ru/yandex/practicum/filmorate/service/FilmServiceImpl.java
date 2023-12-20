package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;
import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

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
        if (inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            validateFilm(film);
            log.debug("Фильм {} обновлен", film);
            return inMemoryFilmStorage.loadFilm(film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = \"%d\\ не найден", film.getId()));
        }
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film addLike(Integer filmId, Integer userId) {
        inMemoryFilmStorage.getFilms().get(filmId).getLikes().add(userId);
        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            if (userId > 0) {
                inMemoryFilmStorage.getFilms().get(filmId).getLikes().remove(userId);
                log.debug("Лайк пользователя {} удален из списка лайков фильма {}", userId, filmId);
                return inMemoryFilmStorage.getFilms().get(filmId);
            }
        }
        throw new FilmNotFoundException(String.format("Фильм с указанным ID = \"%d\\ не найден", filmId));
    }

    public List<Film> findPopularFilms(String count) {
        return inMemoryFilmStorage.getAllFilms().stream()
                .sorted(Film::compareTo)
                .skip(0).limit(Long.parseLong(count)).collect(Collectors.toList());
    }

    public Film getFilmById(Integer filmId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            return inMemoryFilmStorage.getFilms().get(filmId);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = \"%d\\ не найден", filmId));
        }
    }
}
