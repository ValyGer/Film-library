package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

import java.time.LocalDate;

public class UserValidationTest {

    UserController userController = new UserController();

    @Test
    void invalidEmailMissingCharacterAt() { // в мэйле отсутствует @
        User user = new User("mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                        validateUser(user), "Email не содержат символ @");
    }

    @Test
    void blankEmail() { // незаполнен мэйл
        User user = new User("", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
            validateUser(user), "Email пустой");
    }

    @Test
    void invalidLoginWithSpace() { //передан логин с пробелом
        User user = new User("mail@mail.ru", "Log in", "Nike Name", LocalDate.of(1986, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Логин содержит пробел");
    }

    @Test
    void replacingTheNameWithLogin() { //замена имени логином
        User user = new User("mail@mail.ru", "Login", "", LocalDate.of(1986, 10, 25));
        userController.createUser(user);
        Assertions.assertEquals("Login", user.getName(),"Имя должно быть заменено логином");
    }

    @Test
    void incorrectDateOfBirth() { //некорректная дата рождения
        User user = new User("mail@mail.ru", "Login", "Nike Name", LocalDate.of(2024, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Некорректная дата рождения");
    }

    @Test
    void emptyRequest() {  // пустой запрос
        User user = new User();
        Assertions.assertThrows(ValidationException.class, () ->
            validateUser(user), "Передается пустой запрос");
    }
}