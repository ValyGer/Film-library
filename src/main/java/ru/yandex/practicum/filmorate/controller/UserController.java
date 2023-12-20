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

    @GetMapping // Возвращает весь список пользователей
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")  // Возвращает информацию о конкретном пользователе
    public User findUser(@PathVariable("userId") Integer userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}") // Добавление пользователя в друзья
    public User addFriends(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer userFriendId) {
        return userService.addFriends(userId, userFriendId);
    }

    @GetMapping("/{userId}/friends") // Получение списка друзей пользователя
    public List<User> getAllFriendsOfUser(@PathVariable("userId") Integer userId) {
        return userService.getAllFriendsOfUser(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer userFriend) {
        return userService.getCommonFriends(userId, userFriend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriendFromSet(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer userFriend) {
        return userService.deleteFriendFromSet(userId, userFriend);
    }
}
