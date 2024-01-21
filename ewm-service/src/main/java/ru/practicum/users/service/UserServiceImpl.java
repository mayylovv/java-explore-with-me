package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.util.PaginationSetup;
import ru.practicum.handler.NotFoundException;
import ru.practicum.users.dto.NewUserDto;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.Messages.*;
import static ru.practicum.users.dto.UserMapper.toUser;
import static ru.practicum.users.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(NewUserDto userDto) {
        User user = userRepository.save(toUser(userDto));
        log.info(SAVE_MODEL.getMessage(), user);
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info(GET_MODEL_BY_ID.getMessage(), ids);
        if (ids.isEmpty()) {
            return userRepository.findAll(new PaginationSetup(from, size, Sort.unsorted())).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(ids, new PaginationSetup(from, size, Sort.unsorted())).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        log.info(DELETE_MODEL.getMessage(), userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);
    }
}