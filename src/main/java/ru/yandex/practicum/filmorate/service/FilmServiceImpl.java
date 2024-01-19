package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmDbStorage filmDbStorage;
    private final InMemoryFilmStorage inMemoryFilmStorage;

    // Создание нового объекта фильма
    public Film createFilm(Film film) {
        validateFilm(film);
        log.debug("Добавлен новый фильм {}", film);
        return filmDbStorage.createFilm(film);
    }

    // Обновление существующего объекта фильма
    public Film loadFilm(Film film) {
        if (inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            validateFilm(film);
            log.debug("Фильм {} обновлен", film);
            return inMemoryFilmStorage.loadFilm(film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = \"%d\\ не найден", film.getId()));
        }
    }

    // Получение списка фильмов
    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    // Добавление фильму лайка
    public Film addLike(Integer filmId, Integer userId) {
        inMemoryFilmStorage.getFilms().get(filmId).getLikes().add(userId);
        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    // Удаление лайка у фильма
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

    // Получение списка популярных фильмов, задаем количество
    public List<Film> findPopularFilms(String count) {
        return inMemoryFilmStorage.getAllFilms().stream()
                .sorted(Film::compareTo)
                .skip(0).limit(Long.parseLong(count)).collect(Collectors.toList());
    }

    // Получение фильма по ID
    public Film getFilmById(Integer filmId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            return inMemoryFilmStorage.getFilms().get(filmId);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = \"%d\\ не найден", filmId));
        }
    }
}
