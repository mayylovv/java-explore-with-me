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

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping("/comments")
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto createdComment(@PathVariable(value = "userId") Long userId,
                                     @RequestParam(value = "eventId") Long eventId,
                                     @Valid @RequestBody CommentDto commentDto) {
        log.info("Создания комментария = {} по userId = {} и eventId = {}", commentDto, userId, eventId);
        return commentService.saveComment(userId, eventId, commentDto);
    }

    @PatchMapping("/comments/{commentId}")
    @Validated(Marker.OnUpdate.class)
    public CommentDto updateComment(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Обновление комментария {}. Параметра: userId = {} и commentId = {}", commentDto, userId, commentId);
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "commentId") Long commentId) {
        log.info("Получение комментария по id = {} и userId = {}", commentId, userId);
        return commentService.getCommentById(userId, commentId);
    }

    @GetMapping("/comments")
    public Collection<CommentDto> getCommentByUser(@PathVariable(value = "userId") Long userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение комментариев по userId = {}", userId);
        return commentService.getCommentsByUserId(userId, from, size);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "userId") Long userId,
                              @PathVariable(value = "commentId") Long commentId) {
        log.info("Удаление комментариев по id = {} и userId = {}", commentId, userId);
        commentService.deleteCommentById(userId, commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    public Collection<CommentDto> getCommentsByEventId(@PathVariable(value = "userId") Long userId,
                                                       @PathVariable(value = "eventId") Long eventId) {
        log.info("Получение комментариев по userId = {} и eventId = {}", userId, eventId);
        return commentService.getCommentsByEventId(userId, eventId);
    }
}