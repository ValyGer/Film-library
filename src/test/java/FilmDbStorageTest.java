import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.Impl.GenreServiceImpl;
import ru.yandex.practicum.filmorate.service.Impl.RatingServiceImpl;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

@SpringBootTest(classes = FilmorateApplication.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGetFilmById() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1, "Name of Film", "adipisicing", 1000L, LocalDate.of(1965, 3, 25), 1);

        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);

        filmStorage.createFilm(newFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilmById(newFilm.getId());

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdateFilmById() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1, "Name of Film", "adipisicing", 1000L, LocalDate.of(1965, 3, 25), 1);
        Film updateFilm = new Film(newFilm.getId(), "Film Two", "adipisicing two", 30000L, LocalDate.of(1980, 8, 20), 2);

        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);

        filmStorage.createFilm(newFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.loadFilm(updateFilm);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(updateFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllFilms() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1, "Name of Film", "adipisicing", 1000L, LocalDate.of(1965, 3, 25), 1);

        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);

        filmStorage.createFilm(newFilm);

        // вызываем тестируемый метод
        List<Film> allFilm = filmStorage.getAllFilms();

        // проверяем утверждения
        Assertions.assertEquals(allFilm.size(), 1, "В списке фильмов должен быть один фильм");
    }

    @Test
    public void testAddLikeToFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1, "Name of Film", "adipisicing", 1000L, LocalDate.of(1965, 3, 25), 1);
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1994, 10, 25));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);

        filmStorage.createFilm(newFilm);
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        Film film = filmStorage.addLike(newFilm.getId(), newUser.getId());

        // проверяем утверждения
        Assertions.assertEquals(film.getLikes().size(), 1, "Длина списка лайков не совпадает");
        Assertions.assertTrue(film.getLikes().contains(newUser.getId()), "Список лайков не содержит нужного пользователя");
    }

    @Test
    public void testDeleteLikeToFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1, "Name of Film", "adipisicing", 1000L, LocalDate.of(1965, 3, 25), 1);
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1994, 10, 25));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);

        filmStorage.createFilm(newFilm);
        userStorage.createUser(newUser);
        Film film = filmStorage.addLike(newFilm.getId(), newUser.getId());

        // вызываем тестируемый метод
        film = filmStorage.deleteLike(newFilm.getId(), newUser.getId());

        // проверяем утверждения
        Assertions.assertEquals(film.getLikes().size(), 0, "Длина списка лайков должна равняться 0");
    }

    @Test
    public void testFindPopularFilms() {
        // Подготавливаем данные для теста
        Film film1 = new Film(1, "Film One", "adipisicing one", 10000L, LocalDate.of(1975, 5, 15), 1);
        Film film2 = new Film(2, "Film Two", "adipisicing two", 30000L, LocalDate.of(1980, 8, 20), 1);
        Film film3 = new Film(3, "Film Three", "adipisicing three", 5000L, LocalDate.of(1982, 10, 25), 1);
        User user1 = new User(1, "mailuser1@mail.ru", "LoginUser1", "Nike1 Name1", LocalDate.of(1994, 2, 10));
        User user2 = new User(2, "mailuser2@mail.ru", "LoginUser2", "Nike2 Name2", LocalDate.of(1996, 5, 16));
        User user3 = new User(3, "mailuser3@mail.ru", "LoginUser3", "Nike3 Name3", LocalDate.of(1999, 9, 27));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        RatingDbStorage ratingDbStorage = new RatingDbStorage(jdbcTemplate);
        GenreService genreService = new GenreServiceImpl(genreDbStorage);
        RatingService ratingService = new RatingServiceImpl(ratingDbStorage);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreService, ratingService);

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user3.getId());
        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user2.getId());
        filmStorage.addLike(film2.getId(), user3.getId());
        filmStorage.addLike(film3.getId(), user2.getId());

        // вызываем тестируемый метод
        String count = "3";
        List<Film> popularFilms = filmStorage.findPopularFilms(count);

        // проверяем утверждения
        Assertions.assertEquals(popularFilms.size(), 3, "В списке должно находится три объекта");
        Assertions.assertEquals(popularFilms.get(0).getId(), 2, "Самый популярный фильм с ID = 2");
        Assertions.assertEquals(popularFilms.get(1).getId(), 1, "На втором месте стоит фильм с ID = 1");
        Assertions.assertEquals(popularFilms.get(2).getId(), 3, "На третьем месте стоит фильм с ID = 3");

        // вызываем тестируемый метод
        count = "1";
        List<Film> mostPopularFilm = filmStorage.findPopularFilms(count);

        // проверяем утверждения
        Assertions.assertEquals(mostPopularFilm.size(), 1, "В списке должно находится три объекта");
        Assertions.assertEquals(mostPopularFilm.get(0).getId(), 2, "Самый популярный фильм с ID = 2");
    }


    // тестирование методов валидации
    @Test
    // пустое название
    void emptyNameIsPassed() {
        Film film = new Film("", "adipisicing", LocalDate.of(1965, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Передано пустое название");
    }

    @Test
        // описание более 200 символов
    void exceedingMaximumNumberOfCharactersInTheDescription() {
        Film film = new Film("File name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город " +
                "Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 " +
                "миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.of(1965, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Передано описание более 200 символов");
    }

    @Test
        //  Дата выпуска указана раньше чем был выпущен первый фильм
    void yearOfReleaseIsLessThanToday() {
        Film film = new Film("File name", "adipisicing",
                LocalDate.of(1765, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Неверная дата релиза");
    }

    @Test
        // Продолжительность фильма меньше нуля
    void durationIsLessThanZero() {
        Film film = new Film("File name", "adipisicing",
                LocalDate.of(1965, 3, 25), -100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Длительность отрицательная");
    }

    @Test
        // Пустой запрос
    void emptyRequest() {
        Film film = new Film();
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Передается пустой запрос");
    }
}