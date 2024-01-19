package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Getter
@Component
@Qualifier
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;

    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, String> params = Map.of("film_name", film.getName(),
                "description", film.getDescription(),
                "releaseData", film.getReleaseDate().toString(),
                "duration", film.getDuration().toString(),
                "rating_id", film.getMpa().getId().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());
        addGenre(film, film.getGenres());
        return film;
    }

    public void addGenre(Film film, List<Genre> genres) {
        deleteFilmGenresById(film.getId());
        if ((genres.isEmpty()) || (genres == null)) {
            return;
        }  else {
            String sqlRequest = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sqlRequest, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                   ps.setInt(1, film.getId());
                   ps.setInt(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }

    public void deleteFilmGenresById(Integer filmId) {
        String sglQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }



    public Film loadFilm(Film film) {
        return null;
    }

    public Film getFilmById(Integer filmId) {
        return null;
    }

    public void deleteFilm(Integer filmId){

    }

    public List<Film> getAllFilms() {
        return null;
    }
}
