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

    private void checkEmail(User user) {
        if (user.getEmail() == null) {
            throw new ConditionsNotMetException("Email must not be null");
        }
        if (!user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Email must contain @ symbol");
        }
    }

    private void checkLogin(User user) {
        if (user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Login must not be blank");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Login must not contain whitespaces");
        }
    }

    private void checkBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Birthday must not be in the future");
        }
    }

    public Collection<User> findAll() {
        log.info("Find all users");
        return users.values();
    }

    public User create(User user) {
        log.info("Create user initiated");

        checkEmail(user);
        checkLogin(user);
        checkBirthday(user);

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

        if (id == null) {
            throw new ConditionsNotMetException("Id must not be null");
        }

        User currentUser = users.get(id);

        if (currentUser == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        if (user.getLogin() != null) {
            checkLogin(user);
            currentUser.setLogin(user.getLogin());
        }
        if (user.getEmail() != null) {
            checkEmail(user);
            currentUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            checkBirthday(user);
            currentUser.setBirthday(user.getBirthday());
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
