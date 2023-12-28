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
        // В мэйле отсутствует символ @
    void invalidEmailMissingCharacterAt() {
        User user = new User("mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Email не содержат символ @");
    }

    @Test
        // Незаполнен мэйл
    void blankEmail() {
        User user = new User("", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Email пустой");
    }

    @Test
        // Передан логин с пробелом
    void invalidLoginWithSpace() {
        User user = new User("mail@mail.ru", "Log in", "Nike Name", LocalDate.of(1986, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Логин содержит пробел");
    }

    @Test
        // Замена имени логином
    void replacingTheNameWithLogin() {
        User user = new User("mail@mail.ru", "Login", "", LocalDate.of(1986, 10, 25));
        userController.createUser(user);
        Assertions.assertEquals("Login", user.getName(), "Имя должно быть заменено логином");
    }

    @Test
        // Некорректная дата рождения
    void incorrectDateOfBirth() {
        User user = new User("mail@mail.ru", "Login", "Nike Name", LocalDate.of(2024, 10, 25));
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Некорректная дата рождения");
    }

    @Test
        // Пустой запрос
    void emptyRequest() {
        User user = new User();
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Передается пустой запрос");
    }

    @Test
        // Успешное добавление пользователя
    void addNewUserSuccessful() {
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        User user1 = userController.createUser(user);
        Assertions.assertEquals(user1.getEmail(), user.getEmail(), "Электронная почта не совпадает");
        Assertions.assertEquals(user1.getLogin(), user.getLogin(), "Логин не совпадает");
        Assertions.assertEquals(user1.getName(), user.getName(), "Имя не совпадает");
        Assertions.assertEquals(user1.getBirthday(), user.getBirthday(), "Год рождения не совпадает");
    }

    @Test
        //Обновление пользователя из списка
    void updatingUserFromList() {
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
        // Получение списка всех пользователей
    void getAllUsers() {
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        userController.createUser(user);
        List<User> listOfUsers = userController.getAllUsers();
        Assertions.assertEquals(listOfUsers.size(), 1, "В списке пользователей должен быть один пользователь");
    }

    @Test
        // Получение пользователя по ID
    void getUserById() {
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        userController.createUser(user);
        User user1 = userController.findUser(user.getId());
        Assertions.assertEquals(user1.getId(), user.getId(), "ID пользователей не совпадают");
        Assertions.assertEquals(user1.getEmail(), user.getEmail(), "Почта пользователей не совпадает");
        Assertions.assertEquals(user1.getLogin(), user.getLogin(), "Логины пользователей не совпадают");
        Assertions.assertEquals(user1.getName(), user.getName(), "Имена пользователей не совпадают");
        Assertions.assertEquals(user1.getBirthday(), user.getBirthday(), "Дата рождения пользователей не совпадает");
        Assertions.assertEquals(user1.getFriends().size(), user.getFriends().size(), "Длина списка друзей не совпадает");
    }

    @Test
        // Успешное добавление пользователя в друзья
    void successfulAddUserAtFriends() {
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        User friend = new User("friend@mail.ru", "Friend", "Friend Name",
                LocalDate.of(1997, 7, 14));
        userController.createUser(user);
        userController.createUser(friend);
        userController.addFriends(user.getId(), friend.getId());
        Assertions.assertEquals(user.getFriends().size(), 1, "В списке друзей должен быть один пользователей");
        Assertions.assertTrue(user.getFriends().contains(friend.getId()), "У пользователя user должен быть в друзьях пользователь friend");
        Assertions.assertEquals(friend.getFriends().size(), 1, "В списке друзей должен быть один пользователей");
        Assertions.assertTrue(friend.getFriends().contains(user.getId()), "У пользователя friend должен быть в друзьях пользователь user");
    }

    @Test
        // Получение списка друзей пользователя
    void successfulGetListOfFriends() {
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        User friend = new User("friend@mail.ru", "Friend", "Friend Name",
                LocalDate.of(1997, 7, 14));
        userController.createUser(user);
        userController.createUser(friend);
        userController.addFriends(user.getId(), friend.getId());
        List<User> userFriends = userController.getAllFriendsOfUser(user.getId());
        Assertions.assertEquals(userFriends.size(), 1, "Длина списка друзей должна равняться 1");
        Assertions.assertTrue(userFriends.contains(friend), "В списке друзей должен находиться пользователь friend");
    }

    @Test
        // Получение списка общих друзей пользователей
    void getCommonFriendsOfUsers() {
        User user1 = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        User user2 = new User("yandex@mail.ru", "LOG", "Name Lastname",
                LocalDate.of(1990, 5, 9));
        User friend = new User("friend@mail.ru", "Friend", "Friend Name",
                LocalDate.of(1997, 7, 14));
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(friend);
        userController.addFriends(user1.getId(), friend.getId());
        userController.addFriends(user2.getId(), friend.getId());
        List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());
        Assertions.assertEquals(commonFriends.size(), 1, "Общий список друзей должен содержать одного пользователя");
        User commonFriend = commonFriends.get(0);
        Assertions.assertEquals(commonFriend.getId(), friend.getId(), "ID пользователей не совпадают");
        Assertions.assertEquals(commonFriend.getEmail(), friend.getEmail(), "Почта пользователей не совпадает");
        Assertions.assertEquals(commonFriend.getLogin(), friend.getLogin(), "Логины пользователей не совпадают");
        Assertions.assertEquals(commonFriend.getName(), friend.getName(), "Имена пользователей не совпадают");
        Assertions.assertEquals(commonFriend.getBirthday(), friend.getBirthday(), "Дата рождения пользователей не совпадает");
        Assertions.assertEquals(commonFriend.getFriends().size(), friend.getFriends().size(), "Длина списка друзей не совпадает");
    }

    @Test
        // Удаление друга из списка друзей пользователя
    void deleteFriendFromSetFriendsOfUser() {
        User user = new User("mail@mail.ru", "Login", "Nike Name",
                LocalDate.of(1994, 10, 25));
        User friend = new User("friend@mail.ru", "Friend", "Friend Name",
                LocalDate.of(1997, 7, 14));
        userController.createUser(user);
        userController.createUser(friend);
        userController.addFriends(user.getId(), friend.getId());
        userController.deleteFriendFromSet(user.getId(), friend.getId());
        List<User> userFriends = userController.getAllFriendsOfUser(user.getId());
        Assertions.assertEquals(userFriends.size(), 0, "Длина списка друзей должна равняться 0");
    }
}