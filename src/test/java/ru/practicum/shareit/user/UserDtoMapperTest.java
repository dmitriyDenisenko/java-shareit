package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.user.UserTestData.user1;
import static ru.practicum.shareit.user.UserTestData.userDto1;

public class UserDtoMapperTest {
    @Test
    public void toUserDto() {
        UserDto userDto = UserDtoMapper.mapToUserDto(user1);
        assertThat(userDto, equalTo(userDto1));
    }

    @Test
    public void toUser() {
        User user = UserDtoMapper.mapToUser(userDto1);
        assertThat(user, equalTo(user1));
    }
}