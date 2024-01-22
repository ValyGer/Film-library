package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Getter
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(Integer genreId) {
        if (genreId == null) {
            throw new GenreNotFoundException("Передан пустой запрос");
        }
        String sqlRequest = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(sqlRequest, rs -> {
            return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
        }, genreId);
    }

    public List<Genre> getAllGenre() {
        String sqlRequest = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                rs.getString("genre_name")));
    }

    public void deleteGenre(Film film) {
        String sqlRequest = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlRequest, film.getId());
    }

    public void addGenre(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlRequest = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sqlRequest, film.getId(), genre.getId());
            }
        }
    }

    public List<Genre> getGenreForFilm(Integer filmId) {
        String sqlRequest = "SELECT genre_id, genre_name FROM film_genres fg JOIN genres g ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                rs.getString("genre_name")), filmId);
    }
}
