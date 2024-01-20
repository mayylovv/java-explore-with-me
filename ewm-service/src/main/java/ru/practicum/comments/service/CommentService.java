package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto saveComment(Long userId, Long eventId, CommentDto commentDto);

    List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size);

    CommentDto updateComment(Long userId, Long commentId, CommentDto dto);

    CommentDto getCommentById(Long userId, Long commentId);

    void deleteCommentById(Long userId, Long commentId);

    List<CommentDto> getCommentsByEventId(Long userId, Long eventId);

    CommentDto updateCommentAdmin(Long commentId, CommentDto commentDto);

    CommentDto getCommentByIdAdmin(Long commentId);

    void deleteCommentByIdAdmin(Long commentId);

    List<CommentDto> getCommentsAdmin(Long userId, Long eventId, Integer from, Integer size);
}