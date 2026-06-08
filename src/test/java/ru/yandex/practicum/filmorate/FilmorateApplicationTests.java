package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.localDB.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({UserDbStorage.class, UserMapper.class})
class FilmorateApplicationTests {
    @Autowired
    private UserDbStorage userStorage;

    @Test
    public void createAndFindUser_userPersistedInDb() {
        User user = User.builder()
                .name("Test User")
                .email("test@mail.com")
                .login("test_login")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userStorage.create(user);
        Optional<User> foundUser = userStorage.findOne(createdUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(createdUser.getId(), foundUser.get().getId());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
        assertEquals(user.getLogin(), foundUser.get().getLogin());
    }
}
