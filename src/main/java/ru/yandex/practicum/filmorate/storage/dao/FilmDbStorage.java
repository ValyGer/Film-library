package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Component
@Qualifier
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;

    // создание фильма
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

    // добавление значений в промежуточную таблицу связей жанров и фильмов
    public void addGenre(Film film, List<Genre> genres) {
        deleteFilmGenresById(film.getId());
        if ((genres.isEmpty()) || (genres == null)) {
            return;
        } else {
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

    // удаление жанров фильма из таблицы связи фильмов и жанров
    public void deleteFilmGenresById(Integer filmId) {
        String sglQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    // получение фильма по id
    public Film getFilmById(Integer filmId) {
        String sqlRequest = "SELECT f.film_id, f.film_name, f.description, f.releaseData, f.duration, r.rating_id, " +
                "r.rating_name FROM films f JOIN rating r ON r.rating_id = f.rating_id WHERE f.film_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest, filmId);
        if (rs.next()) {
            return filmRowMapper(rs);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = %d не найден", filmId));
        }
    }

    // маппинг фильма
    private Film filmRowMapper(SqlRowSet rs) {
        return new Film(rs.getInt("film_id"), rs.getString("film_name"),
                rs.getString("description"),
                Duration.ofSeconds(rs.getInt("duration")),
                LocalDate.parse(rs.getString("releaseData"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                new Rating(rs.getInt("rating_id"), rs.getString("rating_name")),
                getGenresFilmById(rs.getInt("film_id")));
    }

    // возвращаем список жанров для фильма с определенным id
    private List<Genre> getGenresFilmById(int filmId) {
        String sqlQuery = "SELECT film_genres.film_id, genres.genre_name FROM film_genres " +
                "JOIN genres ON genres.genre_id = film_genres.genre_id WHERE film_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                rs.getString("genre_name")), filmId));
    }

    // обновление фильма
    public Film loadFilm(Film film) {
        getFilmById(film.getId());
        String sqrRequest = "UPDATE films SET film_name = ?, " +
                "description = ?, " +
                "releaseData = ?, " +
                "duration = ?, " +
                "rating_id = ?" +
                " WHERE film_id = ?";
        jdbcTemplate.update(sqrRequest,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        addGenre(film, film.getGenres());
        film.setGenres(getGenres(film.getId()));
        return getFilmById(film.getId());
    }

    // удаление фильма по ID
    public void deleteFilm(Integer filmId) {
        String sqrRequest = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqrRequest);
    }

    // возвращение всех фильмов
    public List<Film> getAllFilms() {
        String sqlRequest = "SELECT * FROM films f " +
                "JOIN rating r ON r.rating_id = f.rating_id " +
                "JOIN film_genres fg ON fg.film_id = f.film_id" +
                "JOIN genres g ON g.genres_id = film_genres.genre_id";
        List<Film> films = jdbcTemplate.query(sqlRequest, (rs, rowNum) -> new Film(rs.getInt("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                Duration.ofSeconds(rs.getInt("duration")),
                LocalDate.parse(rs.getString("releaseData")),
                new Rating(rs.getInt("rating_id"), rs.getString("rating_name")),
                addGenresOfFilm(rs.getInt("film_id"))));
        return films;
    }

    private List<Genre> addGenresOfFilm(int filmId) {
        return null;
    }

/*
    List<Genre> genres = new ArrayList<>();
    Genre genre = new Genre(rs.getInt("g.genres_id"), rs.getString("g.genres_name"));
                genres.add(genre);
*/
}





