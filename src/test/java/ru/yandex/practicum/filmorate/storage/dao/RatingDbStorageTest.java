package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RatingDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void getAllRating() {
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);

        // вызываем тестируемый метод
        List<Rating> ratings = ratingDbStorage.getAllRating();

        // проверяем утверждения
        Assertions.assertEquals(ratings.size(), 5, "В списке рейтингов должно быть 5 рейтингов");
    }

    @Test
    void getRatingById() {
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        Rating newRating = new Rating(3, "PG-13");

        // вызываем тестируемый метод
        Rating rating = ratingDbStorage.getRatingById(3);

        // проверяем утверждения
        assertThat(rating)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newRating);        // и сохраненного пользователя - совпадают
    }
}