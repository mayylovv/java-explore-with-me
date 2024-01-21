package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.handler.NotFoundException;
import ru.practicum.handler.ValidateException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;
import ru.practicum.util.PaginationSetup;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comments.dto.CommentMapper.mapToComment;
import static ru.practicum.comments.dto.CommentMapper.mapToCommentDto;
import static ru.practicum.events.EventState.PUBLISHED;
import static ru.practicum.util.Messages.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private Comment getByIdAndAuthorId(Long commentId, Long userId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));
    }

    private Comment getById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));
    }

    @Transactional
    @Override
    public CommentDto saveComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        // нельзя добавить комментарий в неопубликованном событии
        if (!event.getState().equals(PUBLISHED)) {
            throw new ValidateException("Event not published.");
        }
        Comment comment = commentRepository.save(mapToComment(user, event, commentDto));
        log.info(SAVE_MODEL.getMessage(), comment);
        return mapToCommentDto(comment);
    }

    // все комментарии автора
    @Override
    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        log.info(GET_MODELS.getMessage());
        PageRequest pageable = new PaginationSetup(from, size, Sort.unsorted());
        return commentRepository.findAllByAuthorId(userId, pageable).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    // обновление комментария автором
    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto dto) {
        Comment comment = getByIdAndAuthorId(commentId, userId);
        comment.setText(dto.getText());
        log.info(UPDATE_MODEL.getMessage(), comment);
        return mapToCommentDto(commentRepository.save(comment));
    }

    // комментарий автора по идентификатору
    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        Comment comment = getByIdAndAuthorId(commentId, userId);
        log.info(GET_MODEL_BY_ID.getMessage(), commentId);
        return mapToCommentDto(comment);
    }

    // удаление комментария автором
    @Transactional
    @Override
    public void deleteCommentById(Long userId, Long commentId) {
        getByIdAndAuthorId(commentId, userId);
        commentRepository.deleteById(commentId);
        log.info(DELETE_MODEL.getMessage(), commentId);
    }

    // все комментарии по событию инициатора
    @Override
    public List<CommentDto> getCommentsByEventId(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    // редактирование комментария админом
    @Override
    public CommentDto updateCommentAdmin(Long commentId, CommentDto commentDto) {
        Comment comment = getById(commentId);
        comment.setText(commentDto.getText());
        log.info(UPDATE_MODEL.getMessage(), comment);
        return mapToCommentDto(commentRepository.save(comment));
    }

    //запрос комментария по ид админом
    @Override
    public CommentDto getCommentByIdAdmin(Long commentId) {
        Comment comment = getById(commentId);
        log.info(GET_MODEL_BY_ID.getMessage(), commentId);
        return mapToCommentDto(comment);
    }

    // удаление комментария админом
    @Override
    public void deleteCommentByIdAdmin(Long commentId) {
        getById(commentId);
        commentRepository.deleteById(commentId);
        log.info(DELETE_MODEL.getMessage(), commentId);
    }

    // запрос комментарий или всех, или по автору, или по событию
    @Override
    public List<CommentDto> getCommentsAdmin(Long userId, Long eventId, Integer from, Integer size) {
        log.info(GET_MODELS.getMessage());
        PageRequest pageable = new PaginationSetup(from, size, Sort.unsorted());
        return commentRepository.findAllByAuthorIdOrEventId(userId, eventId, pageable).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }
}