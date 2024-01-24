package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@Component
@Qualifier
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;
    private final RatingService ratingService;

    // создание фильма
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, String> params = Map.of("film_name", film.getName(),
                "description", film.getDescription(),
                "releaseDate", film.getReleaseDate().toString(),
                "duration", film.getDuration().toString(),
                "rating_id", film.getMpa().getId().toString());
        film.setId(simpleJdbcInsert.executeAndReturnKey(params).intValue());
        film.setMpa(ratingService.getRatingById(film.getMpa().getId()));
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
            genreService.addGenreAtFilm(film);
        }
        return film;
    }

    // получение фильма по id
    public Film getFilmById(Integer filmId) {
        if (filmId == null) {
            throw new FilmNotFoundException("Передан пустой запрос");
        }
        String sqlRequest = "SELECT f.film_id, f.film_name, f.description, f.releaseDate, f.duration, r.rating_id, " +
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
                rs.getLong("duration"),
                LocalDate.parse(rs.getString("releaseDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                rs.getInt("rating_id"),
                genreService.getGenreForFilm(rs.getInt("film_id")));
    }

    // обновление фильма
    public Film loadFilm(Film film) {
        getFilmById(film.getId());
        String sqlRequest = "UPDATE films SET film_name = ?, " +
                "description = ?, " +
                "releaseDate = ?, " +
                "duration = ?, " +
                "rating_id = ?" +
                " WHERE film_id = ?";
        jdbcTemplate.update(sqlRequest,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        film.setMpa(ratingService.getRatingById(film.getMpa().getId()));
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            Set<Genre> setGenres = new LinkedHashSet<>(genres);
            film.setGenres(new ArrayList<>(setGenres));
            for (Genre genre : setGenres) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
            genreService.addGenreAtFilm(film);
        }
        return film;
    }

    // возвращение всех фильмов
    public List<Film> getAllFilms() {
        String sqlRequest = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sqlRequest, (rs, rowNum) -> new Film(rs.getInt("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getLong("duration"),
                LocalDate.parse(rs.getString("releaseDate")),
                rs.getInt("rating_id"),
                genreService.getGenreForFilm(rs.getInt("film_id"))));
        return films;
    }

    // добавление лайка к фильму
    public Film addLike(Integer filmId, Integer userId) {
        if (filmId == null) {
            throw new FilmNotFoundException("Передан пустой запрос");
        }
        String sqlRequest = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        if (jdbcTemplate.update(sqlRequest, filmId, userId) > 0) {
            Film film = getFilmById(filmId);
            film.getLikes().add(userId);
            return film;
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = %d не найден", filmId));
        }
    }

    // удаление лайка у фильма
    public Film deleteLike(Integer filmId, Integer userId) {
        if (filmId == null) {
            throw new FilmNotFoundException("Передан пустой запрос");
        }
        String sqlRequest = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        if (jdbcTemplate.update(sqlRequest, filmId, userId) > 0) {
            Film film = getFilmById(filmId);
            film.getLikes().remove(userId);
            return film;
        } else {
            throw new FilmNotFoundException(String.format("Фильм с указанным ID = %d не найден", filmId));
        }
    }

    // получение списка лайков у фильма
    public Set<Integer> getLikesOfFilm(Integer filmId) {
        String sqlRequest = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new TreeSet<>(jdbcTemplate.query(sqlRequest, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    // получение популярного фильма и списка популярных фильмов
    public List<Film> findPopularFilms(String count) {
        String sqlRequest = "SELECT f.film_id, f.film_name, f.description, f.releaseDate, f.duration, f.rating_id FROM films f " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id GROUP BY f.FILM_ID ORDER BY COUNT(fl.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> {
            return new Film(rs.getInt("film_id"),
                    rs.getString("film_name"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("releaseDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    Duration.ofSeconds(rs.getInt("duration")),
                    getLikesOfFilm(rs.getInt("film_id")),
                    ratingService.getRatingById(rs.getInt("rating_id")),
                    genreService.getGenreForFilm(rs.getInt("film_id")));
        }, count);
    }
}




