package ru.practicum.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;


import java.util.List;


public interface CommentService {
    CommentDto saveComment(Long userId, Long eventId, NewCommentDto newCommentDto);


    CommentDto updateComment(Long userId, Long commentId, NewCommentDto commentDto);


    CommentDto updateCommentByAdmin(Long commentId, NewCommentDto commentDto);


    CommentDto getCommentById(Long userId, Long commentId);


    List<CommentDto> getCommentsUser(Long userId, PageRequest page);


    List<CommentDto> getCommentsEvent(Long userId, Long eventId, PageRequest page);


    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);
}
