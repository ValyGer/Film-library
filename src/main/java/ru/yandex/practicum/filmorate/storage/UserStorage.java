package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {

    User createUser(User user);

    User loadUser(User user);

    User getUserById(Integer userId);

    void deleteUser(Integer userId);

    List<User> getAllUsers();

    User addFriend(Integer userId, Integer userFriendId);

    User deleteFriend(Integer userId, Integer userFriendId);

    List<User> getAllFriendsOfUser(Integer userId);

    List<User> getCommonFriends(Integer userFirstId, Integer userSecondId);
}