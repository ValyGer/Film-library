package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    User findUserById(int id);

    void deleteUser(int id);

    List<User> getAllUsers();
}