package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class FilmValidationTest {
    FilmValidation filmValidation = new FilmValidation();

    @Test
    void emptyNameIsPassed(){ // пустое название
        Film film = new Film("", "adipisicing",
                LocalDate.of(1965, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                filmValidation.validateFilm(film), "Передано пустое название");
    }
    @Test
    void exceedingMaximumNumberOfCharactersInTheDescription() { // описание более 200 символов
        Film film = new Film("File name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город " +
                "Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 " +
                "миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.of(1965, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                filmValidation.validateFilm(film), "Передано описание более 200 символов");
    }
    @Test
    void yearOfReleaseIsLlessThantoday(){
        Film film = new Film("File name", "adipisicing",
                LocalDate.of(1765, 3, 25), 100L);
        Assertions.assertThrows(ValidationException.class, () ->
                filmValidation.validateFilm(film), "Неверная дата релиза");
    }
    @Test
    void durationIsLessThanZero() {
        Film film = new Film("File name", "adipisicing",
                LocalDate.of(1965, 3, 25), -100L);
        Assertions.assertThrows(ValidationException.class, () ->
                filmValidation.validateFilm(film), "Длительность отрицательная");
    }
    @Test
    void EmptyRequest(){  // пустой запрос
        Film film = new Film();
        Assertions.assertThrows(ValidationException.class, () ->
                filmValidation.validateFilm(film), "Передается пустой запрос");
    }
}