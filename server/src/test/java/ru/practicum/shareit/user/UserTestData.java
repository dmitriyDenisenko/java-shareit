package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserTestData {
    public static final UserDto userDto1 = new UserDto(1L, "user1", "user1@mail.ru");
    public static final UserDto userDto2 = new UserDto(2L, "user2", "user2@mail.ru");
    public static final UserDto userDto3 = new UserDto(3L, "user3", "user3@mail.ru");
    public static final UserDto userDtoCreated = new UserDto(4L, "userCreated", "userCreated@mail.ru");
    public static final User user1 = new User(1L, "user1", "user1@mail.ru");
    public static final User user2 = new User(2L, "user2", "user2@mail.ru");
}