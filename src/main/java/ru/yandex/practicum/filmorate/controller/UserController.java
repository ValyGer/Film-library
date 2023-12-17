package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping // Добавление нового пользователя
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping // Обновление информации о пользователе или создание нового
    public User loadUser(@RequestBody User user) {
        return userService.loadUser(user);
    }

    @GetMapping // Возвращает весь список фильмов
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
