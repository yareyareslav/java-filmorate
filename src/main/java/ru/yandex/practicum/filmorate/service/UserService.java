package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Service
public class UserService {
    private final HashMap<Long, User> users = new HashMap<>();

    private void checkLogin(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Login must not contain whitespaces");
        }
    }

    public Collection<User> findAll() {
        log.info("Find all users");
        return users.values();
    }

    public User create(User user) {
        log.info("Create user initiated");

        checkLogin(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("User created");
        return user;
    }

    public User update(User user) {
        Long id = user.getId();
        log.info("Update user initiated. User id: {}", id);

        User currentUser = users.get(id);

        if (currentUser == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        String login = user.getLogin();
        String email = user.getEmail();
        String name = user.getName();
        LocalDate birthday = user.getBirthday();

        if (login != null && !login.isBlank()) {
            checkLogin(user);
            currentUser.setLogin(login);
        }
        if (email != null && !email.isBlank()) {
            currentUser.setEmail(email);
        }
        if (name != null && !name.isBlank()) {
            currentUser.setName(name);
        }
        if (birthday != null) {
            currentUser.setBirthday(birthday);
        }

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
