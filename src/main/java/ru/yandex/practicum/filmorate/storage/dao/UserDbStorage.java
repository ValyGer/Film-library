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
import java.util.List;
import java.util.Map;

@Getter
@Component
@Qualifier
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;

   public User createUser(User user) {
       String sqlRequest = "INSERT INTO user (genre_id, genre_name)";
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

    public User updateUser(User user) {
        return null;
    }

    public User findUserById(int userId) {
        String sqlRequest = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlRequest, userId);
        if (rs.next()) {
            return userMap(rs);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с указанным ID = %d не найден", userId));
        }
    }

    public void deleteUser(int id) {

    }

    public List<User> getAllUsers() {
       return null;
    }

    private User userMap(SqlRowSet rs) {
       return new User(rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                LocalDate.parse(rs.getString("birthday")));
    }
}
