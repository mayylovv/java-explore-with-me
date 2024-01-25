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
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class CommentControllerPrivate {

    private final CommentService commentService;
    private static final Sort sort = Sort.by("created").descending();
    static final String COMMENT_PATH = "/{commentId}";

    @PostMapping
    public CommentDto saveComment(@PathVariable Long userId,
                                  @RequestParam Long eventId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Создание нового комментария {}", newCommentDto);
        return commentService.saveComment(userId, eventId, newCommentDto);
    }

    @PatchMapping(COMMENT_PATH)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto commentDto) {
        log.info("Обновление комментария с id = {} пользователем с userId = {}, новый комментарий: {}", commentId, userId, commentDto);
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @GetMapping(COMMENT_PATH)
    public CommentDto getComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Получение комментария с commentId = {} пользователя c userId = {}", commentId, userId);
        return commentService.getCommentById(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsByUserId(@PathVariable Long userId,
                                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from, size, sort);
        log.info("Получение комментариев пользователя с id = {}", userId);
        return commentService.getCommentsByUserId(userId, page);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsForEvent(@PathVariable Long userId,
                                                @PathVariable Long eventId,
                                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from, size, sort);
        log.info("Получение комментариев для события с eventId = {} пользователя с userId = {}", eventId, userId);
        return commentService.getCommentsForEvent(userId, eventId, page);
    }

    @DeleteMapping(COMMENT_PATH)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Удаления комментария с commentId = {} у пользователя с userId =  {}", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }
}
