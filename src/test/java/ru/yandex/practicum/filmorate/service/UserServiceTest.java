package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    public void addFriend_noSuchFriend_returnTrue() {
        User user1 = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userStorage.create(User.builder()
                .name("Test 2")
                .email("test-2@mail.com")
                .login("Test-2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        boolean result = userService.addFriend(user1.getId(), user2.getId());

        assertTrue(result);
        assertTrue(user1.getFriendsIds().contains(user2.getId()));
        assertTrue(user2.getFriendsIds().contains(user1.getId()));
    }

    @Test
    public void addFriend_hasSuchFriend_returnFalse() {
        User user1 = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userStorage.create(User.builder()
                .name("Test 2")
                .email("test-2@mail.com")
                .login("Test-2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriendsIds().contains(user2.getId()));
        assertTrue(user2.getFriendsIds().contains(user1.getId()));

        boolean result = userService.addFriend(user2.getId(), user1.getId());

        assertFalse(result);
        assertTrue(user1.getFriendsIds().contains(user2.getId()));
        assertTrue(user2.getFriendsIds().contains(user1.getId()));
    }

    @Test
    public void addFriend_invalidId_throwsNotFoundException() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        assertThrows(NotFoundException.class, () -> userService.addFriend(user.getId(), 999L));
        assertThrows(NotFoundException.class, () -> userService.addFriend(999L, user.getId()));
    }

    @Test
    public void removeFriend_hasSuchFriend_returnTrue() {
        User user1 = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userStorage.create(User.builder()
                .name("Test 2")
                .email("test-2@mail.com")
                .login("Test-2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriendsIds().contains(user2.getId()));
        assertTrue(user2.getFriendsIds().contains(user1.getId()));

        boolean result = userService.removeFriend(user1.getId(), user2.getId());

        assertTrue(result);
        assertFalse(user1.getFriendsIds().contains(user2.getId()));
        assertFalse(user2.getFriendsIds().contains(user1.getId()));
    }

    @Test
    public void removeFriend_noSuchFriend_returnFalse() {
        User user1 = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userStorage.create(User.builder()
                .name("Test 2")
                .email("test-2@mail.com")
                .login("Test-2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        boolean result = userService.removeFriend(user1.getId(), user2.getId());

        assertFalse(result);
        assertFalse(user1.getFriendsIds().contains(user2.getId()));
        assertFalse(user2.getFriendsIds().contains(user1.getId()));
    }

    @Test
    public void removeFriend_invalidId_throwsNotFoundException() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        assertThrows(NotFoundException.class, () -> userService.removeFriend(user.getId(), 999L));
        assertThrows(NotFoundException.class, () -> userService.removeFriend(999L, user.getId()));
    }

    @Test
    public void getFriends_returnCollectionOfFriends() {
        User user = userStorage.create(User.builder()
                .name("Test")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User friend1 = userStorage.create(User.builder()
                .name("Friend 1")
                .email("friend-1@mail.com")
                .login("Friend-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User friend2 = userStorage.create(User.builder()
                .name("Friend 2")
                .email("friend-2@mail.com")
                .login("Friend-2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        userService.addFriend(user.getId(), friend1.getId());
        userService.addFriend(user.getId(), friend2.getId());

        Collection<User> friends = userService.getFriends(user.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.contains(friend1));
        assertTrue(friends.contains(friend2));
    }

    @Test
    public void getFriends_invalidId_throwsNotFoundException() {
        userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        assertThrows(NotFoundException.class, () -> userService.getFriends(999L));
    }

    @Test
    public void getCommonFriends_returnCollectionOfCommonFriends() {
        User user1 = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user2 = userStorage.create(User.builder()
                .name("Test 2")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        User friend1 = userStorage.create(User.builder()
                .name("Friend 1")
                .email("friend-1@mail.com")
                .login("Friend-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User friend2 = userStorage.create(User.builder()
                .name("Friend 2")
                .email("friend-2@mail.com")
                .login("Friend-2")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        userService.addFriend(user1.getId(), friend1.getId());
        userService.addFriend(user1.getId(), friend2.getId());
        userService.addFriend(user2.getId(), friend1.getId());

        Collection<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(friend1));
        assertFalse(commonFriends.contains(friend2));
    }

    @Test
    public void getCommonFriends_invalidId_throwsNotFoundException() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());

        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(user.getId(), 999L));
        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(999L, user.getId()));
    }
}
