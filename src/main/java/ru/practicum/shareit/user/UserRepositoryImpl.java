package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private List<User> users = new ArrayList<>();
    private int id = 0;

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User saveUser(User user) {
        if (isUniqueEmail(user)) {
            user.setId(getId());
            users.add(user);
            return user;
        }
        throw new UserValidationEmailError();
    }

    @Override
    public User getUserById(int id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                return users.get(i);
            }
        }
        throw new UserNotExistsError();
    }

    @Override
    public User updateUser(User user, int userId) {
        User updatingUser = getUserById(userId);
        user.setId(updatingUser.getId());
        if (user.getName() == null) {
            user.setName(updatingUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(updatingUser.getEmail());
        } else {
            if (!isUniqueEmail(user)) {
                throw new UserValidationEmailError();
            }
        }
        users.remove(updatingUser);
        users.add(user);
        return user;
    }

    @Override
    public void removeUser(int id) {
        User user = getUserById(id);
        users.remove(user);
    }

    private long getId() {
        id++;
        return id;
    }

    private boolean isUniqueEmail(User user) {
        return users.stream().filter(a -> a.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList()).isEmpty();

    }

}
