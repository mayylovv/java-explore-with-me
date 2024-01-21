package ru.practicum.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;
import ru.practicum.util.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    // авторизованный пользователь добавляет комментарий
    @PostMapping("/comments")
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto createdComment(@PathVariable(value = "userId") Long userId,
                                     @RequestParam(value = "eventId") Long eventId,
                                     @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {} by user Id {} and event Id {}", commentDto, userId, eventId);
        return commentService.saveComment(userId, eventId, commentDto);
    }

    // редактирование комментария автором
    @PatchMapping("/comments/{commentId}")
    @Validated(Marker.OnUpdate.class)
    public CommentDto updateComment(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Updating comment {}. Parameters: userId {} and comment Id {}", commentDto, userId, commentId);
        return commentService.updateComment(userId, commentId, commentDto);
    }

    // запрос комментария по идентификатору автором
    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "commentId") Long commentId) {
        log.info("Get comments by Id {} and user Id {}", commentId, userId);
        return commentService.getCommentById(userId, commentId);
    }

    // запрос всех комментарий автором
    @GetMapping("/comments")
    public Collection<CommentDto> getCommentByUser(@PathVariable(value = "userId") Long userId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get comments by user Id {}", userId);
        return commentService.getCommentsByUserId(userId, from, size);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "userId") Long userId,
                              @PathVariable(value = "commentId") Long commentId) {
        log.info("Delete comments by Id {} and user Id {}", commentId, userId);
        commentService.deleteCommentById(userId, commentId);
    }

    // запрос комментарий по событию инициатором события
    @GetMapping("/events/{eventId}/comments")
    public Collection<CommentDto> getCommentsByEventId(@PathVariable(value = "userId") Long userId,
                                                       @PathVariable(value = "eventId") Long eventId) {
        log.info("Get comments by user Id {} and event id {}", userId, eventId);
        return commentService.getCommentsByEventId(userId, eventId);
    }
}