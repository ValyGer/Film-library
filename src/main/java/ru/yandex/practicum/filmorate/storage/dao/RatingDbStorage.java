package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Component
@AllArgsConstructor
public class RatingDbStorage implements RatingStorage {

    private JdbcTemplate jdbcTemplate;

    public List<Rating> getAllRating() {
        String sqlRequest = "SELECT * FROM rating";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) ->
                new Rating(rs.getInt("rating_id"), rs.getString("rating_name")));
    }

    public Rating getRatingById(Integer ratingId) {
        if (ratingId == null) {
            throw new RatingNotFoundException("Передан пустой запрос");
        }
        String sqlRequest = "SELECT * FROM rating WHERE rating_id = ?";
        return jdbcTemplate.queryForObject(sqlRequest, (rs, rowNum) ->
                new Rating(rs.getInt("id"), rs.getString("name")), ratingId);
    }
}
