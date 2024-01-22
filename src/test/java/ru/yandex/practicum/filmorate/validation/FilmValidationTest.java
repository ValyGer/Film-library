package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.Impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.Impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

public class FilmValidationTest {

        private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        private final FilmService filmService = new FilmServiceImpl(inMemoryFilmStorage);
        private final FilmController filmController = new FilmController(filmService);
        private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        private final UserService userService = new UserServiceImpl(inMemoryUserStorage);
        private final UserController userController = new UserController(userService);

    @Test // пустое название
    void emptyNameIsPassed() {
        Film film = new Film("", "adipisicing",
                LocalDate.of(1965, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Передано пустое название");
    }

    @Test // описание более 200 символов
    void exceedingMaximumNumberOfCharactersInTheDescription() {
        Film film = new Film("File name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город " +
                "Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 " +
                "миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.of(1965, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Передано описание более 200 символов");
    }

    @Test //  Дата выпуска указана раньше чем был выпущен первый фильм
    void yearOfReleaseIsLessThanToday() {
        Film film = new Film("File name", "adipisicing",
                LocalDate.of(1765, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Неверная дата релиза");
    }

    @Test // Продолжительность фильма меньше нуля
    void durationIsLessThanZero() {
        Film film = new Film("File name", "adipisicing",
                LocalDate.of(1965, 3, 25), -100L);
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Длительность отрицательная");
    }

    @Test // Пустой запрос
    void emptyRequest() {
        Film film = new Film();
        Assertions.assertThrows(ValidationException.class, () ->
                validateFilm(film), "Передается пустой запрос");
    }

    @Test // Успешное добавление нового фильма
    void addNewFilmSuccessful() {
        Film film = new Film("Name of Film", "adipisicing",
                LocalDate.of(1965, 3, 25), 1000L);
        Film film1 = filmController.createFilm(film);
        Assertions.assertEquals(film1.getName(), film.getName(), "Некорректное название");
        Assertions.assertEquals(film1.getDescription(), film.getDescription(), "Некорректное описание");
        Assertions.assertEquals(film1.getReleaseDate(), film.getReleaseDate(), "Некорректное время выхода");
        Assertions.assertEquals(film1.getDuration(), film.getDuration(), "Некорректная продолжительность");
    }

    @Test // Обновление фильма из списка
    void updatingMovieFromList() {
        Film film = new Film("Name of Film", "adipisicing",
                LocalDate.of(1965, 3, 25), 1000L);
        filmController.createFilm(film);
        Film film1 = new Film(film.getId(), "NEW Name of Film", "NEW adipisicing",
                LocalDate.of(2005, 8, 7), 17000L);
        Film film2 = filmController.loadFilm(film1);
        Assertions.assertEquals(film2.getId(), 1, "В списке должен быть обновлен фильм с индексом 1");
        Assertions.assertEquals(film2.getName(), film1.getName(), "Название фильма обновлено");
        Assertions.assertEquals(film2.getDescription(), film1.getDescription(), "Описание фильма не откорректировано");
        Assertions.assertEquals(film2.getReleaseDate(), film1.getReleaseDate(), "Не изменено время выхода");
        Assertions.assertEquals(film2.getDuration(), film1.getDuration(), "Не изменена продолжительность");
    }

    @Test // Получение списка всех фильмов
    void getAllFilms() {
        Film film = new Film("Name of Film", "adipisicing",
                LocalDate.of(1965, 3, 25), 1000L);
        filmController.createFilm(film);
        List<Film> listOfFilms = filmController.getAllFilms();
        Assertions.assertEquals(listOfFilms.size(), 1, "В списке фильмов должен быть один фильм");
    }

    @Test // Возвращает фильм по id
    void getFilmsById() {
        Film film = new Film("Name of Film", "adipisicing",
                LocalDate.of(1965, 3, 25), 1000L);
        filmController.createFilm(film);
        Film film1 = filmController.getFilmById(film.getId());
        Assertions.assertEquals(film1.getId(), film.getId(), "ID фильмов не совпадают");
        Assertions.assertEquals(film1.getName(), film.getName(), "Название фильмов не совпадает");
        Assertions.assertEquals(film1.getDescription(), film.getDescription(), "Описание фильмов не совпадает");
        Assertions.assertEquals(film1.getReleaseDate(), film.getReleaseDate(), "Дата выхода не совпадает");
        Assertions.assertEquals(film1.getDuration(), film.getDuration(), "Продолжительность фильмов не совпадает");
        Assertions.assertEquals(film1.getLikes().size(), film.getLikes().size(), "Длина списков лайков не совпадает");
    }

    @Test // Добавление лайка к фильму пользователем
    void addLikeToFilm() {
        Film film = new Film("Name of Film", "adipisicing",
                LocalDate.of(1965, 3, 25), 1000L);
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        filmController.createFilm(film);
        userController.createUser(user);
        filmController.addLike(film.getId(),user.getId());
        Assertions.assertEquals(film.getLikes().size(), 1, "Длина списка лайков не совпадает");
        Assertions.assertTrue(film.getLikes().contains(user.getId()), "Список лайков не содержит нужного пользователя");
    }

    @Test // Удаление лайка у фильма пользователем
    void deleteLikeFromFilm() {
        Film film = new Film("Name of Film", "adipisicing",
                LocalDate.of(1965, 3, 25), 1000L);
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        filmController.createFilm(film);
        userController.createUser(user);
        filmController.addLike(film.getId(),user.getId());
        filmController.deleteLike(film.getId(),user.getId());
        Assertions.assertEquals(film.getLikes().size(), 0, "Длина списка лайков должна равняться 0");
    }

    @Test // Получение списка популярных фильмов
    void getSetOfPopularFilms() {
        Film film1 = new Film("Film One", "adipisicing one",
                LocalDate.of(1975, 5, 15), 10000L);
        Film film2 = new Film("Film Two", "adipisicing two",
                LocalDate.of(1980, 8, 20), 30000L);
        Film film3 = new Film("Film Three", "adipisicing three",
                LocalDate.of(1982, 10, 25), 5000L);
        User user1 = new User("mailuser1@mail.ru", "LoginUser1", "Nike1 Name1",
                LocalDate.of(1994, 2, 10));
        User user2 = new User("mailuser2@mail.ru", "LoginUser2", "Nike2 Name2",
                LocalDate.of(1996, 5, 16));
        User user3 = new User("mailuser3@mail.ru", "LoginUser3", "Nike3 Name3",
                LocalDate.of(1999, 9, 27));
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film3);
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film1.getId(), user3.getId());
        filmController.addLike(film2.getId(), user1.getId());
        filmController.addLike(film2.getId(), user2.getId());
        filmController.addLike(film2.getId(), user3.getId());
        filmController.addLike(film3.getId(), user2.getId());
        String count = "3";
        List<Film> popularFilms = filmController.findPopularFilms(count);
        Assertions.assertEquals(popularFilms.size(), 3, "В списке должно находится три объекта");
        Assertions.assertEquals(popularFilms.get(0).getId(), 7, "Самый популярный фильм с ID = 7");
        Assertions.assertEquals(popularFilms.get(1).getId(), 6, "На втором месте стоит фильм с ID = 6");
        Assertions.assertEquals(popularFilms.get(2).getId(), 8, "На третьем месте стоит фильм с ID = 8");
        count = "1";
        popularFilms = filmController.findPopularFilms(count);
        Assertions.assertEquals(popularFilms.size(), 1, "В списке должен находится один объект");
        Assertions.assertEquals(popularFilms.get(0).getId(), 7, "Самый популярный фильм с ID=7");
    }
}