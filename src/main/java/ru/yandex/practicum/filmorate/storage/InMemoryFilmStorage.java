package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static int generateFilmId = 0;

    public Film createFilm(Film film) {
        validateFilm(film);
        film.setId(++generateFilmId);
        films.put(film.getId(), film);
        return film;
    }

    public Film loadFilm(Film film) {
        validateFilm(film);
        Film saved = films.get(film.getId());
        if (saved == null) {
            throw new ValidationException("Фильм не найден");
        } else {
            films.put(saved.getId(), film);
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
