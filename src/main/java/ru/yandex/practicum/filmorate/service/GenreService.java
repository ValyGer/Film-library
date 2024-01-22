package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {

    Genre getGenreById(Integer genreId);

    void addGenreAtFilm(Film film);

    List<Genre> getGenreForFilm(Integer filmId);

    List<Genre> getAllGenre();
}
