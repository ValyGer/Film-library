package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
        if (inMemoryUserStorage.getUsers().containsKey(user.getId())) {
            validateUser(user);
            log.debug("Пользователь {} обновлен", user);
            return inMemoryUserStorage.loadUser(user);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с указанным ID = \"%d\\ не найден", user.getId()));
        }
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User findUserById(Integer userId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId)) {
            return inMemoryUserStorage.getUsers().get(userId);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с указанным ID = \"%d\\ не найден", userId));
        }
    }

    public List<User> getCommonFriends(Integer userId, Integer userFriend) {
         TreeSet<Integer> intersectionFriends = inMemoryUserStorage.getUsers().get(userId).getFriends().stream()
                 .filter(s1 -> inMemoryUserStorage.getUsers().get(userFriend).getFriends().contains(s1))
                 .collect(Collectors.toCollection(TreeSet::new));
         return intersectionFriends.stream()
                 .map(id -> inMemoryUserStorage.getUsers().get(id))
                 .collect(Collectors.toCollection(ArrayList::new));
    }

    public User addFriends(Integer userId, Integer userFriendId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId)) {
            if (userFriendId > 0) {
                    inMemoryUserStorage.getUsers().get(userId).getFriends().add(userFriendId);
                    inMemoryUserStorage.getUsers().get(userFriendId).getFriends().add(userId);
                    log.debug("Пользователь {} добавлен в друзья пользователя {}", userFriendId, userId);
                    return inMemoryUserStorage.getUsers().get(userId);
                }
        }
        throw new UserNotFoundException(String.format("Пользователь с указанным ID = \"%d\\ не найден", userId));
    }

    public List<User> getAllFriendsOfUser(Integer userId) {
        return inMemoryUserStorage.getUsers().get(userId).getFriends().stream()
                .map(id -> inMemoryUserStorage.getUsers().get(id))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public User deleteFriendFromSet(Integer userId, Integer userFriend) {
        inMemoryUserStorage.getUsers().get(userId).getFriends().remove(userFriend);
        return inMemoryUserStorage.getUsers().get(userId);
    }
}
