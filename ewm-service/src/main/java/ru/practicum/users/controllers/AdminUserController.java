package ru.practicum.users.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.NewUserDto;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto saveUser(@Valid @RequestBody NewUserDto userDto) {
        log.info("POST '/admin/users'. Запрос на добавление нового пользователя с телом {} ", userDto);
        UserDto response = userService.createUser(userDto);
        log.info("POST '/admin/users'. Ответ, новый пользователь: {}, успешно добавлен ", response);
        return response;
    }

    @GetMapping()
    public Collection<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                        @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET '/admin/users'. Запрос на получение пользователя с id: {}", ids);
        Collection<UserDto> response = userService.getUsers(ids, from, size);
        log.info("GET '/admin/users'. Ответ, пользователь c id: {}, {} ", ids, response);
        return response;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info("DELETE '/admin/users/{userId}'. Запрос на удаление пользователя с id {} ", userId);
        userService.deleteUserById(userId);
        log.info("DELETE '/admin/users/{userId}'. Ответ, пользователь с id {}, успешно удален ", userId);
    }
}
