package ru.yandex.practicum.filmorate.storage.dao;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUser;

@Getter
@Component
@Qualifier
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    // создание пользователя
    public User createUser(User user) {
        validateUser(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, String> params = Map.of("email", user.getEmail(),
                "login", user.getLogin(),
                "user_name", user.getName(),
                "birthday", user.getBirthday().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        return user;
    }

    // обновление информации о пользователе
    public User loadUser(User user) {
        getUserById(user.getId());
        String sqlRequest = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlRequest, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    // получение пользователя по id
    public User getUserById(Integer userId) {
        String sqlRequest = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest, userId);
        if (rs.next()) {
            return userRowMapper(rs);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с указанным ID = %d не найден", userId));
        }
    }

    // получение всех пользователей
    public List<User> getAllUsers() {
        String sqlRequest = "SELECT * FROM users";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(userRowMapper(rs));
        }
        return users;
    }

    // удаление пользователя
    public void deleteUser(Integer userId) {
        String sqlRequest = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlRequest, userId);
    }

    // вспомогательный RowMapper для пользователя
    private User userRowMapper(SqlRowSet rs) {
        return new User(rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                LocalDate.parse(rs.getString("birthday")));
    }

    // обработка информации о друзьях
    // добавление в друзья
    public User addFriend(Integer userId, Integer userFriendId) {
        if (userFriendId < 0) {
            throw new UserNotFoundException("Передан некорректный запроc");
        }
        User friend = getUserById(userFriendId);
        if (friend.getFriends().contains(userId)) {
            String sqlRequest = "UPDATE friendship_status SET user_id = ? AND friend_id = ? AND status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlRequest, userId, userFriendId, true, userId, userFriendId);
        }
        String sqlRequest = "INSERT INTO friendship_status (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlRequest, userId, userFriendId, false);
        return getUserById(userId);
    }

    // удаление из друзей
    public User deleteFriend(Integer userId, Integer userFriendId) {
        if (userFriendId < 0) {
            throw new UserNotFoundException("Передан некорректный запроc");
        }
        getUserById(userId);
        User friend = getUserById(userFriendId);
        String sqlRequest = "DELETE FROM friendship_status WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlRequest, userId, userFriendId);
        if (friend.getFriends().contains(userId)) {
            sqlRequest = "INSERT INTO friendship_status (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlRequest, userId, userFriendId, false);
        }
        return getUserById(userId);
    }

    // вывод всех друзей пользователя
    public List<User> getAllFriendsOfUser(Integer userId) {
        getUserById(userId);
        String sqlRequest = "SELECT * FROM users u JOIN friendship_status fs ON fs.friend_id = u.user_id WHERE fs.user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest, userId);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(userRowMapper(rs));
        }
        return users;
    }

    // вывод общих друзей у пользователей
    public List<User> getCommonFriends(Integer userFirstId, Integer userSecondId) {
        getUserById(userFirstId);
        getUserById(userSecondId);
        List<User> friendFirstUser = getAllFriendsOfUser(userFirstId);
        List<User> secondFirstUser = getAllFriendsOfUser(userSecondId);
        List<User> commonFriends = new ArrayList<>(friendFirstUser);
        commonFriends.retainAll(secondFirstUser);
        return commonFriends;
    }
}
