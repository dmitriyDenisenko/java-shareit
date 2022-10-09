package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDtoMapper;
import ru.practicum.shareit.user.exception.UserNotExistsError;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<UserDto> getAllUsers() {
        return UserDtoMapper.mapToUserDto(userRepository.findAll());
    }


    @Override
    @Transactional
    public UserDto addUser(UserDto user) {
        return UserDtoMapper.mapToUserDto(userRepository.save(UserDtoMapper.mapToUser(user)));

    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto user, Long userId) {
        User oldUser = userRepository.findById(userId).orElseThrow(UserNotExistsError::new);
        User updUser = UserDtoMapper.mapToUser(user);
        if (updUser.getName() != null) {
            oldUser.setName(updUser.getName());
        }
        if (updUser.getEmail() != null) {
            oldUser.setEmail(updUser.getEmail());
        }
        return UserDtoMapper.mapToUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserDtoMapper.mapToUserDto(user.get());
        }
        throw new UserNotExistsError();

    }

    @Transactional
    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }
}
