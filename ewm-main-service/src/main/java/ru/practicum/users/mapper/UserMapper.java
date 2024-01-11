package ru.practicum.users.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.users.dto.NewUserDto;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.ShortUserDto;
import ru.practicum.users.model.User;

@UtilityClass
public class UserMapper {

    public static User toUser(NewUserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }

    public static ShortUserDto toUserShortDto(User user) {
        ShortUserDto shortUserDto = new ShortUserDto(
                user.getId(),
                user.getName()
        );
        return shortUserDto;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto(
                user.getEmail(),
                user.getId(),
                user.getName()
        );
        return userDto;
    }
}