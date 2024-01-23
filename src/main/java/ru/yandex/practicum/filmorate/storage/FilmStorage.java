package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;


public interface FilmStorage {

    Film createFilm(Film film);

    Film getFilmById(Integer filmId);

    Film loadFilm(Film film);

    List<Film> getAllFilms();

    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);

    Set<Integer> getLikesOfFilm(Integer filmId);

    List<Film> findPopularFilms(String count);
}
