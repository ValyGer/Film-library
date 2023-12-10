package ru.yandex.practicum.filmorate.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


public class UserValidation {

    private static final  Logger log = LoggerFactory.getLogger(UserValidation.class);

    public static void validateUser(User user) {
        if (user != null) {
            if (((user.getEmail() == null) || (user.getEmail().isBlank()))) {
                log.debug("Email не должен быть пустым");
                throw new ValidationException("Email не должен быть пустым");
            } else if (!user.getEmail().contains("@")) {
                log.debug("Email должен содержать символ @");
                throw new ValidationException("Email должен содержать символ @");
            }
            if (user.getLogin().contains(" ")) {
                log.debug("Логин не должен содержать пробел");
                throw new ValidationException("Логин не должен содержать пробел");
            }
            if ((user.getName() == null) || user.getName().isBlank()) {
                log.debug("Имя не заполнено. Заменено логином");
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.debug("Некорректная дата рождения. Дата рождения указана в будущем");
                throw new ValidationException("Некорректная дата рождения. Дата рождения указана в будущем");
            }
        } else {
            log.debug("Пустой запрос. Заполните данные");
            throw new ValidationException("Пустой запрос. Заполните данные");
        }
    }
}
