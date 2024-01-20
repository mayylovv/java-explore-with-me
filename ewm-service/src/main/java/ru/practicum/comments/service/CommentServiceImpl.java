package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;

import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidateException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;
import ru.practicum.util.PaginationSetup;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comments.mapper.CommentMapper.mapToComment;
import static ru.practicum.comments.mapper.CommentMapper.mapToCommentDto;
import static ru.practicum.events.enums.EventState.PUBLISHED;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public CommentDto saveComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new ValidateException("Событие не было опубликовано.");
        }
        Comment comment = commentRepository.save(mapToComment(user, event, commentDto));
        log.info("Сохранение {}", comment);
        return mapToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        log.info("Получение");
        PageRequest pageable = new PaginationSetup(from, size, Sort.unsorted());
        return commentRepository.findAllByAuthorId(userId, pageable).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto dto) {
        Comment comment = getByIdAndAuthorId(commentId, userId);
        comment.setText(dto.getText());
        log.info("Обновление {}", comment);
        return mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        Comment comment = getByIdAndAuthorId(commentId, userId);
        log.info("Получение по id = {}", commentId);
        return mapToCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentById(Long userId, Long commentId) {
        getByIdAndAuthorId(commentId, userId);
        commentRepository.deleteById(commentId);
        log.info("Удаление по id = {}", commentId);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateCommentAdmin(Long commentId, CommentDto commentDto) {
        Comment comment = getById(commentId);
        comment.setText(commentDto.getText());
        log.info("Обновление {}", comment);
        return mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentByIdAdmin(Long commentId) {
        Comment comment = getById(commentId);
        log.info("Получение по id = {}", commentId);
        return mapToCommentDto(comment);
    }

    @Override
    public void deleteCommentByIdAdmin(Long commentId) {
        getById(commentId);
        commentRepository.deleteById(commentId);
        log.info("Удаление по id = {}", commentId);
    }

    @Override
    public List<CommentDto> getCommentsAdmin(Long userId, Long eventId, Integer from, Integer size) {
        log.info("Получение");
        PageRequest pageable = new PaginationSetup(from, size, Sort.unsorted());
        return commentRepository.findAllByAuthorIdOrEventId(userId, eventId, pageable).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getByIdAndAuthorId(Long commentId, Long userId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не был найден."));
    }

    private Comment getById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не был найден."));
    }
}