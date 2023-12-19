package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

@Getter
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int generateUserId = 0;

    public User createUser(User user) {
        validateUser(user);
        user.setId(++generateUserId);
        users.put(user.getId(), user);
        return user;
    }

    public User loadUser(User user) {
        validateUser(user);
        User saved = users.get(user.getId());
        if (saved == null) {
            throw new ValidationException("Пользователь не найден");
        } else {
            users.put(saved.getId(), user);
        }
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
