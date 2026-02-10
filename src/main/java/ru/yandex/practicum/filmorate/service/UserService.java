package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

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
            throw new ConditionsNotMetException("Name must not be blank");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Name must not contain whitespaces");
        }
    }

    private void checkBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Birthday must not be in the future");
        }
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        checkEmail(user);
        checkLogin(user);
        checkBirthday(user);

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User user) {
        Long id = user.getId();

        if (id == null) {
            throw new ConditionsNotMetException("Id must not be null");
        }

        User currentUser = users.get(id);

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
