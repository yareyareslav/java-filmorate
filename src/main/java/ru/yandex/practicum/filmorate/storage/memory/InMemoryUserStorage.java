package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Find all users");
        return users.values();
    }

    public Optional<User> findOne(Long id) {
        log.info("Find one user initiated");
        User user = users.get(id);

        if (user == null) {
            return Optional.empty();
        }

        log.info("User was found. User id: {}", id);
        return Optional.of(user);
    }

    public User create(User user) {
        log.info("Create user initiated");

        user.setId(getNextId());
        user.setFriendsIds(new HashSet<>());
        users.put(user.getId(), user);

        log.info("User created");
        return user;
    }

    public User update(User user) {
        Long id = user.getId();
        log.info("Update user initiated. User id: {}", id);

        User currentUser = users.get(id);

        currentUser.setLogin(user.getLogin());
        currentUser.setEmail(user.getEmail());
        currentUser.setName(user.getName());
        currentUser.setBirthday(user.getBirthday());

        users.put(id, currentUser);

        log.info("User updated. User id: {}", id);
        return currentUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
