package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(Integer genreId);

    void deleteGenre(Film film);

    void addGenre(Film film);

    List<Genre> getGenreForFilm(Integer filmId);

    List<Genre> getAllGenre();
}
