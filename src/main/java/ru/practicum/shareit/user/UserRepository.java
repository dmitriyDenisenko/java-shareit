package ru.practicum.shareit.user;

import java.util.List;


public interface UserRepository {
    List<User> getAllUsers();

    User saveUser(User user);

    User updateUser(User user, int userId);

    User getUserById(int id);

    void removeUser(int id);
}
