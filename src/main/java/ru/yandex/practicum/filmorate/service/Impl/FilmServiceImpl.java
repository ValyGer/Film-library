package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmDbStorage filmDbStorage;

    // Создание нового объекта фильма
    public Film createFilm(Film film) {
        validateFilm(film);
        log.debug("Добавлен новый фильм {}", film);
        return filmDbStorage.createFilm(film);
    }

    // Получение фильма по ID
    public Film getFilmById(Integer filmId) {
        return filmDbStorage.getFilmById(filmId);
    }

    // Обновление существующего объекта фильма
    public Film loadFilm(Film film) {
        validateFilm(film);
        log.debug("Фильм {} обновлен", film);
        return filmDbStorage.loadFilm(film);
    }

    // Получение списка фильмов
    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    // Добавление фильму лайка
    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmDbStorage.addLike(filmId, userId);
        log.debug("Лайк пользователя {} удален из списка лайков фильма {}", userId, filmId);
        return film;
    }

    // Удаление лайка у фильма
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmDbStorage.deleteLike(filmId, userId);
        log.debug("Лайк пользователя {} удален из списка лайков фильма {}", userId, filmId);
        return film;
    }

    // Получение списка популярных фильмов, задаем количество
    public List<Film> findPopularFilms(String count) {
        return filmDbStorage.findPopularFilms(count);
    }
}
