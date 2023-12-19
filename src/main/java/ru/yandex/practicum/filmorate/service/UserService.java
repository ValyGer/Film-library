package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User createUser(User user);

    User loadUser(User user);

    List<User> getAllUsers();

    User addFriends(Integer userId, Integer userFriendId);

    User findUserById(Integer userId);

    List<User> getAllFriendsOfUser(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer userFriend);

    User deleteFriendFromSet(Integer userId, Integer userFriend);
}
