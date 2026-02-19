package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addFriend(Long id, Long friendId) {
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        return true;
    }

    public boolean removeFriend(Long id, Long friendId) {
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        return true;
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        Set<User> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(friend.getFriends());

        return commonFriends;
    }

}
