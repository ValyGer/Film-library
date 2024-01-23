package ru.yandex.practicum.filmorate.service.Impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreDbStorage genreDbStorage;

    public Genre getGenreById(Integer genreId) {
        return genreDbStorage.getGenreById(genreId);
    }

    public void addGenreAtFilm(Film film) {
        genreDbStorage.deleteGenre(film);
        genreDbStorage.addGenre(film);
    }

    public List<Genre> getGenreForFilm(Integer filmId) {
        return genreDbStorage.getGenreForFilm(filmId);
    }

    public List<Genre> getAllGenre() {
        return new ArrayList<>(genreDbStorage.getAllGenre());
    }
}
