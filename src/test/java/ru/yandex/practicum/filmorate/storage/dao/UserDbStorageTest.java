package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

@SpringBootTest(classes = FilmorateApplication.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGetUserById() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.getUserById(newUser.getId());

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdateUserById() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        User updateUser = new User(newUser.getId(), "NEWmail@mail.ru", "NEWLogin", "NEW Nike Name", LocalDate.of(2001, 12, 7));

        // вызываем тестируемый метод
        User savedUser = userStorage.loadUser(updateUser);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(updateUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllUsers() {
        // Подготавливаем данные для теста
        User newUser = new User("mail@mail.ru", "Login", "Nike Name", LocalDate.of(1994, 10, 25));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        List<User> listOfUsers = userStorage.getAllUsers();

        // проверяем список пользователей
        Assertions.assertEquals(listOfUsers.size(), 1, "В списке пользователей должен быть один пользователь");
    }

    @Test
    public void testDeleteUser() {
        // Подготавливаем данные для теста
        User newUser = new User("mail@mail.ru", "Login", "Nike Name", LocalDate.of(1994, 10, 25));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        userStorage.deleteUser(newUser.getId());

        // проверяем список пользователей
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userStorage.getUserById(newUser.getId()), "Пользователь успешно удален");
    }

    @Test
    public void testAddFriend() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        User newFriend = new User(2, "friend@mail.ru", "Friend", "Friend Name", LocalDate.of(1997, 7, 14));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);
        userStorage.createUser(newFriend);

        // вызываем тестируемый метод
        userStorage.addFriend(newUser.getId(), newFriend.getId());
        List<User> friendsOfUser = userStorage.getAllFriendsOfUser(newUser.getId());

        // проверяем утверждения
        Assertions.assertEquals(friendsOfUser.size(), 1, "В списке друзей должен быть один пользователей");
        Assertions.assertTrue(friendsOfUser.contains(newFriend), "У пользователя c id = 1 должен быть в друзьях пользователь c id = 2");
    }

    @Test
    public void testGetCommonFriends() {
        // Подготавливаем данные для теста
        User newUser1 = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        User newUser2 = new User(2, "yandex@mail.ru", "LOG", "Name Lastname", LocalDate.of(1990, 5, 9));
        User friend = new User(3, "friend@mail.ru", "Friend", "Friend Name", LocalDate.of(1997, 7, 14));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);
        userStorage.createUser(friend);
        userStorage.addFriend(newUser1.getId(), friend.getId());
        userStorage.addFriend(newUser2.getId(), friend.getId());

        // вызываем тестируемый метод
        List<User> commonFriends = userStorage.getCommonFriends(newUser1.getId(), newUser2.getId());

        // проверяем утверждения
        Assertions.assertEquals(commonFriends.size(), 1, "В списке общих друзей должен быть один пользователей");
        Assertions.assertTrue(commonFriends.contains(friend), "У пользователя c id = 1 и id = 2 должен быть в общий друг пользователь c id = 3");
    }

    @Test
    public void testDeleteFriend() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));
        User newFriend = new User(2, "friend@mail.ru", "Friend", "Friend Name", LocalDate.of(1997, 7, 14));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);
        userStorage.createUser(newFriend);
        userStorage.addFriend(newUser.getId(), newFriend.getId());

        // вызываем тестируемый метод
        userStorage.deleteFriend(newUser.getId(), newFriend.getId());
        List<User> friendsOfUser = userStorage.getAllFriendsOfUser(newUser.getId());

        // проверяем утверждения
        Assertions.assertEquals(friendsOfUser.size(), 0, "В списке друзей не должно быть пользователей");
    }


    // тестирование методов валидации
    @Test
    // В мэйле отсутствует символ @
    void invalidEmailMissingCharacterAt() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail.ru", "Login", "Nike Name", LocalDate.of(1986, 10, 25));

        // проверка метода валидации
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(newUser), "Email не содержат символ @");
    }

    @Test
        // Незаполнен мэйл
    void blankEmail() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "", "Login", "Nike Name", LocalDate.of(1986, 10, 25));

        // проверка метода валидации
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(newUser), "Email пустой");
    }

    @Test
        // Передан логин с пробелом
    void invalidLoginWithSpace() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Log in", "Nike Name", LocalDate.of(1986, 10, 25));

        // проверка метода валидации
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(newUser), "Логин содержит пробел");
    }

    @Test
        // Замена имени логином
    void replacingTheNameWithLogin() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Login", "", LocalDate.of(1986, 10, 25));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        User savedUser = userStorage.getUserById(newUser.getId());

        // Проверяем заменено ли имя логином
        Assertions.assertEquals("Login", savedUser.getName(), "Имя должно быть заменено логином");
    }

    @Test
        // Некорректная дата рождения
    void incorrectDateOfBirth() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "mail@mail.ru", "Login", "Nike Name", LocalDate.of(2024, 10, 25));

        // проверка метода валидации
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(newUser), "Некорректная дата рождения");
    }

    @Test
        // Пустой запрос
    void emptyRequest() {
        // Подготавливаем данные для теста
        User user = new User();

        // проверка метода валидации
        Assertions.assertThrows(ValidationException.class, () ->
                validateUser(user), "Передается пустой запрос");
    }
}