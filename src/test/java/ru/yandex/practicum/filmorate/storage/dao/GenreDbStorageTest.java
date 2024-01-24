package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.Impl.GenreServiceImpl;
import ru.yandex.practicum.filmorate.service.Impl.RatingServiceImpl;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenreDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGetGenreById() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        Genre newGenre = new Genre(1, "Комедия");

        // вызываем тестируемый метод
        Genre genre = genreDbStorage.getGenreById(1);

        // проверяем утверждения
        assertThat(genre)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newGenre);        // и сохраненного пользователя - совпадают
    }

    @Test
    void testGetAllGenre() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);

        // вызываем тестируемый метод
        List<Genre> genres = genreDbStorage.getAllGenre();

        // проверяем утверждения
        Assertions.assertEquals(genres.size(), 6, "В списке жанров должно быть 6 жанров");
    }

    @Test
    void getGenreForFilm() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1, null));
        genres.add(new Genre(3, null));
        Film newFilm = new Film(1, "Name of Film", "adipisicing", 1000L, LocalDate.of(1965, 3, 25), 1, genres);
        filmStorage.createFilm(newFilm);

        // вызываем тестируемый метод
        List<Genre> genres1 = genreDbStorage.getGenreForFilm(newFilm.getId());

        // проверяем результат
        Assertions.assertEquals(genres1.size(), 2, "В списке жанров должно быть 2 элемента");
    }
}