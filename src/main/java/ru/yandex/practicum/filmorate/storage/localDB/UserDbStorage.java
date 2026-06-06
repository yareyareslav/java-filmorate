package ru.yandex.practicum.filmorate.storage.localDB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_ONE_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER_QUERY = """ 
            INSERT INTO users(email, login, name, birthday)
            VALUES(?, ?, ?, ?)
            """;
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_USER_FRIENDS_QUERY =  """
            SELECT u.id AS id, u.name AS name, u.email AS email, u.login AS login, u.birthday AS birthday
            FROM user_friends AS uf
            JOIN users AS u ON u.id = uf.friend_id AND uf.user_id = ?
            """;
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT u.id AS id, u.name AS name, u.email AS email, u.login AS login, u.birthday AS birthday
            FROM user_friends AS uf
            JOIN user_friends AS of ON of.friend_id = uf.friend_id AND uf.user_id = ? AND of.user_id = ?
            JOIN users AS u ON u.id = uf.friend_id
            """;
    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO user_friends(user_id, friend_id)
            VALUES(?, ?)
            """;
    private static final String DELETE_FRIEND_QUERY = """
            DELETE FROM user_friends
            WHERE user_id = ? AND friend_id = ?
            """;

    private final JdbcTemplate jdbc;

    private static final UserMapper mapper = new UserMapper();

    @Override
    public Collection<User> findAll() {
        log.trace("Searching for all users");
        return jdbc.query(FIND_ALL_QUERY, mapper).stream().toList();
    }

    @Override
    public Optional<User> findOne(Long id) {
        try {
            log.trace("Searching for user with id {}", id);
            User user = jdbc.queryForObject(FIND_ONE_QUERY, mapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            log.info("User with id {} is not found", id);
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_USER_QUERY, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null) {
            return null;
        }
        long userId = keyHolder.getKey().longValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(UPDATE_USER_QUERY);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            ps.setLong(5, id);
            return ps;
        });
        return user;
    }

    @Override
    public User delete(Long id) {
        Optional<User> deletedUser = findOne(id);
        if (deletedUser.isPresent()) {
            jdbc.update(DELETE_USER_QUERY, mapper, id);
        }
        return deletedUser.orElse(null);
    }

    @Override
    public Collection<User> findFriends(Long userId) {
        return jdbc.query(FIND_USER_FRIENDS_QUERY, mapper, userId);
    }

    @Override
    public Collection<User> findCommonFriends(Long userId, Long friendId) {
        return jdbc.query(FIND_COMMON_FRIENDS_QUERY, mapper, userId, friendId);
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        int rowsAffected = jdbc.update(INSERT_FRIEND_QUERY, userId, friendId);
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        int rowsAffected = jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
        return rowsAffected > 0;
    }
}
