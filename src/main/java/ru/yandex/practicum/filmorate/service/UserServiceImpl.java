package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private InMemoryUserStorage inMemoryUserStorage;

    public User createUser(User user) {
        validateUser(user);
        log.debug("Добавлен новый пользователь {}", user);
        return inMemoryUserStorage.createUser(user);
    }

    public User loadUser(User user) {
        validateUser(user);
        log.debug("Пользователь {} обновлен", user);
        return inMemoryUserStorage.loadUser(user);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }
}
