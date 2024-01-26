package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserDbStorage userDbStorage;

    // Создание нового объекта пользователя
    public User createUser(User user) {
        validateUser(user);
        log.debug("Добавлен новый пользователь {}", user);
        return userDbStorage.createUser(user);
    }

    // Обновление существующего объекта пользователя
    public User loadUser(User user) {
        validateUser(user);
        log.debug("Пользователь {} обновлен", user);
        return userDbStorage.loadUser(user);
    }

    // Получение списка пользователей
    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    // Получение пользователя по ID
    public User findUserById(Integer userId) {
        return userDbStorage.getUserById(userId);
    }

    // друзья не доделаны!!!

    // Получение списка общих друзей пользователей
    public List<User> getCommonFriends(Integer userId, Integer userFriend) {
        return userDbStorage.getCommonFriends(userId, userFriend);
    }

    // Добавление пользователя в друзья
    public User addFriend(Integer userId, Integer userFriendId) {
        log.debug("Пользователь {} добавлен в друзья пользователя {}", userFriendId, userId);
        return userDbStorage.addFriend(userId, userFriendId);
    }

    // Получение списка друзей пользователя
    public List<User> getAllFriendsOfUser(Integer userId) {
        return userDbStorage.getAllFriendsOfUser(userId);
    }

    // Удаление пользователя из друзей
    public User deleteFriend(Integer userId, Integer userFriend) {
        return userDbStorage.deleteFriend(userId, userFriend);
    }
}
