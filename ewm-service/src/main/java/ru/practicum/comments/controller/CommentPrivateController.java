package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private static final Sort sort = Sort.by("created").descending();
    private final CommentService commentService;

    @PostMapping
    public CommentDto saveComment(@PathVariable Long userId, @RequestParam Long eventId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST '/users/{userId}/comments'. Запрос на добавление нового комментария {} ", newCommentDto);
        CommentDto response = commentService.saveComment(userId, eventId, newCommentDto);
        log.info("POST '/users/{userId}/comments'. Ответ, пользователь " + userId +
                " добавил новый комментарий событию: {}", eventId);
        return response;
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                    @RequestBody NewCommentDto commentDto) {
        log.info("PATCH '/users/{userId}/comments/{commentId}'. Запрос на обновление комментария {} ", commentDto);
        CommentDto response = commentService.updateComment(userId, commentId, commentDto);
        log.info("PATCH '/users/{userId}/comments/{commentId}'. Ответ, пользователь " + userId +
                " обновил комментарий: {}", commentId);
        return response;
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("GET '/users/{userId}/comments/{commentId}'. Запрос комментария {} пользователя {}", commentId, userId);
        CommentDto response = commentService.getCommentById(userId, commentId);
        log.info("GET '/users/{userId}/comments/{commentId}. Ответ, пользователь " + userId +
                " получил комментарий: {}", commentId);
        return response;
    }

    @GetMapping
    public List<CommentDto> getCommentsUser(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from, size, sort);
        log.info("GET '/users/{userId}/comments/'. Запрос комментариев пользователя {}", userId);
        List<CommentDto> response = commentService.getCommentsUser(userId, page);
        log.info("GET '/users/{userId}/comments/'. Ответ, пользователь " + userId + " получил все свои комментарий");
        return response;
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from, size, sort);
        log.info("GET '/users/{userId}/comments/events/{eventId}'. Запрос комментариев события {} пользователя {}", eventId, userId);
        List<CommentDto> response = commentService.getCommentsEvent(userId, eventId, page);
        log.info("GET '/users/{userId}/comments/events/{eventId}'. Ответ, получены все комментарии к событию: {}", eventId);
        return response;
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("DELETE '/users/{userId}/comments/{commentId}'. Запрос удаления комментария {} у пользователя {}", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }
}
