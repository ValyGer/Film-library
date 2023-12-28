package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

public class UserValidationTest {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserService userService = new UserServiceImpl(inMemoryUserStorage);
    private final UserController userController = new UserController(userService);

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
        Assertions.assertEquals("Login", user.getName(), "Имя должно быть заменено логином");
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

    @Test
    void addNewUserSuccessful() { // Успешное добавление пользователя
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        User user1 = userController.createUser(user);
        Assertions.assertEquals(user1.getEmail(), user.getEmail(), "Электронная почта не совпадает");
        Assertions.assertEquals(user1.getLogin(), user.getLogin(), "Логин не совпадает");
        Assertions.assertEquals(user1.getName(), user.getName(), "Имя не совпадает");
        Assertions.assertEquals(user1.getBirthday(), user.getBirthday(), "Год рождения не совпадает");
    }

    @Test
    void updatingUserFromList() { //Обновление пользователя из списка
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        userController.createUser(user);
        User user1 = new User(user.getId(), "NEWmail@mail.ru", "NEWLogin", "NEW Nike Name",
                LocalDate.of(2001, 12, 7));
        User user2 = userController.loadUser(user1);
        Assertions.assertEquals(user2.getEmail(), user1.getEmail(), "Электронная почта не обновлено");
        Assertions.assertEquals(user2.getLogin(), user1.getLogin(), "Логин не обновлен");
        Assertions.assertEquals(user2.getName(), user1.getName(), "Имя не обновлено");
        Assertions.assertEquals(user2.getBirthday(), user1.getBirthday(), "Год рождения не обновлен");
    }

    @Test
    void getAllUsers() {  // Получение списка всех пользователей
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        userController.createUser(user);
        List<User> listOfUsers = userController.getAllUsers();
        Assertions.assertEquals(listOfUsers.size(), 1, "В списке пользователей должен быть один пользователь");
    }
}