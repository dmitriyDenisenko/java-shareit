package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
