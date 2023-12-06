package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final  Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int generateUserId = 0;
    private final UserValidation userValidation = new UserValidation();

    @PostMapping // Добавление нового пользователя
    public User createUser(@RequestBody User user) {
        userValidation.validateUser(user);
        user.setId(++generateUserId);
        users.put(user.getId(), user);
        log.debug("Добавлен новый пользователь {}", user);
        return user;
    }

    @PutMapping // Обновление информации о пользователе или создание нового
    public User loadUser(@RequestBody User user) {
        userValidation.validateUser(user);
        User saved = users.get(user.getId());
        if (saved == null) {
            log.debug("Пользователь не найден {}", user);
            throw new ValidationException("Пользователь не найден");
        } else {
            users.put(saved.getId(), user);
            log.debug("Пользователь {} обновлен", user);
        }
        return user;
    }

    @GetMapping // Возвращает весь список фильмов
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


}
