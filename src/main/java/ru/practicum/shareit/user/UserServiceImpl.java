package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User addUser(User user) {
        return userRepository.saveUser(user);
    }

    @Override
    public User updateUser(User user, int userId) {
        return userRepository.updateUser(user, userId);
    }

    @Override
    public User findUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void removeUser(int id) {
        userRepository.removeUser(id);
    }
}
