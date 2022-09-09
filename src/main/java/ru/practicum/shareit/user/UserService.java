package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user, int userId);

    User findUserById(int id);

    void removeUser(int id);
}
