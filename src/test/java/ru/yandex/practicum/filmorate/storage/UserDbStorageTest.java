package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.localDB.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({UserDbStorage.class, UserMapper.class})
public class UserDbStorageTest {
    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void findAll_returnUserArray() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        assertEquals(0, userDbStorage.findAll().size());

        userDbStorage.create(user);
        userDbStorage.create(User.builder()
                .name("Test 2")
                .email("test2@mail.com")
                .login("Test2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        userDbStorage.create(User.builder()
                .name("Test 3")
                .email("test3@mail.com")
                .login("Test3")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        assertEquals(3, userDbStorage.findAll().size());
    }

    @Test
    public void create_validData_returnUserWithId() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        User createdUser = userDbStorage.create(user);

        assertTrue(userDbStorage.findAll().contains(createdUser));
        assertNotNull(createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }

    @Test
    public void addFriend_andDeleteFriend_workCorrectly() {
        User user1 = userDbStorage.create(User.builder()
                .name("User 1")
                .email("user1@mail.com")
                .login("user1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userDbStorage.create(User.builder()
                .name("User 2")
                .email("user2@mail.com")
                .login("user2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        assertTrue(userDbStorage.addFriend(user1.getId(), user2.getId()));
        assertThrows(DuplicateKeyException.class,
                () -> userDbStorage.addFriend(user1.getId(), user2.getId()));

        Collection<User> friends = userDbStorage.findFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.iterator().next().getId());

        assertTrue(userDbStorage.deleteFriend(user1.getId(), user2.getId()));
        assertTrue(userDbStorage.findFriends(user1.getId()).isEmpty());
    }

    @Test
    public void findCommonFriends_returnMutualFriends() {
        User user1 = userDbStorage.create(User.builder()
                .name("User 1")
                .email("user1@mail.com")
                .login("user1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userDbStorage.create(User.builder()
                .name("User 2")
                .email("user2@mail.com")
                .login("user2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User friend = userDbStorage.create(User.builder()
                .name("Friend")
                .email("friend@mail.com")
                .login("friend")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        userDbStorage.addFriend(user1.getId(), friend.getId());
        userDbStorage.addFriend(user2.getId(), friend.getId());

        Collection<User> commonFriends = userDbStorage.findCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(friend.getId(), commonFriends.iterator().next().getId());
    }
}
