package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public class UserDbStorage extends BaseStorage<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_ONE_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES(?, ?, ?, ?) RETURNING id";
    private static final String UPDATE_USER_QUERY = "UPDATE films SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public Optional<User> findOne(Long id) {
        return findOne(FIND_ONE_QUERY, id);
    }

    public User create(User user) {
        long id = insert(INSERT_USER_QUERY, user);
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(UPDATE_USER_QUERY, user);
        return user;
    }
}
