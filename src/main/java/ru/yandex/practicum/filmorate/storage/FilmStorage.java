package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;


public interface FilmStorage {

    Film createFilm(Film film);

    void addGenre(Film film, List<Genre> genres);

    void deleteFilmGenresById(Integer filmId);

    Film loadFilm(Film film);

    Film getFilmById(Integer filmId);

    void deleteFilm(Integer filmId);

    List<Film> getAllFilms();
}
