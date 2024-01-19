package ru.yandex.practicum.filmorate.storage.dao;


import lombok.AllArgsConstructor;
import lombok.Getter;
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

@Getter
@Component
@Qualifier
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;

   public User createUser(User user) {
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

    public User loadUser(User user) {
        getUserById(user.getId());
        String sqlRequest = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlRequest, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    public User getUserById(Integer userId) {
        String sqlRequest = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest, userId);
        if (rs.next()) {
            return userRowMapper(rs);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с указанным ID = %d не найден", userId));
        }
    }

    public List<User> getAllUsers() {
        String sqlRequest = "SELECT * FROM users";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(userRowMapper(rs));
        }
        return users;
    }

    public void deleteUser(Integer userId) {
        String sqlRequest = "DELETE * FROM users WHERE user_id = ?";
        jdbcTemplate.queryForRowSet(sqlRequest, userId);
    }

    private User userRowMapper(SqlRowSet rs) {
       return new User(rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                LocalDate.parse(rs.getString("birthday")));
    }
}
