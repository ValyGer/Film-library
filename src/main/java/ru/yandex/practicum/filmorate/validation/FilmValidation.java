package ru.yandex.practicum.filmorate.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidation {

    private static final  Logger log = LoggerFactory.getLogger(FilmValidation.class);
    private static final int  MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate START_FIRST_FILM = LocalDate.of(1895, 12, 28);

    public void validateFilm(Film film) {
        if (film != null) {
            if ((film.getName() == null) || film.getName().isBlank()) {
                log.debug("Название не может быть пустым");
                throw new ValidationException("Название не может быть пустым");
            }
            if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                log.debug("Превышена максимальная длина описания. " +
                        "Описание должно быть не более" + MAX_DESCRIPTION_LENGTH + " символов.");
                throw new ValidationException("Превышена максимальная длина описания. " +
                        "Описание должно быть не более" + MAX_DESCRIPTION_LENGTH + " символов.");
            }
            if (film.getReleaseDate().isBefore(START_FIRST_FILM)) {
                log.debug("Некорректная дата выхода. " +
                        "Дата релиза должна быть не ранее " + START_FIRST_FILM);
                throw new ValidationException("Некорректная дата выхода. " +
                        "Дата релиза должна быть не ранее " + START_FIRST_FILM);
            }
            if (film.getDuration() <= 0) {
                log.debug("Продолжительность фильма должна быть положительной");
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
        } else {
            log.debug("Пустой запрос. Заполните данные");
            throw new ValidationException("Пустой запрос. Заполните данные");
        }
    }
}
