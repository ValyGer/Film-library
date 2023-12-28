package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.FilmValidation.validateFilm;

public class FilmValidationTest {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmServiceImpl(inMemoryFilmStorage);
    private final FilmController filmController = new FilmController(filmService);

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
}