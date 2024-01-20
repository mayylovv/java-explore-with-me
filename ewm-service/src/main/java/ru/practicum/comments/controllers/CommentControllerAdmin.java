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
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class CommentControllerAdmin {

    private final CommentService commentService;

    @GetMapping
    public Collection<CommentDto> getComments(@RequestParam(required = false) Long userId,
                                              @RequestParam(required = false) Long eventId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.debug("Получение комментариев по userId = {} и eventId = {}", userId, eventId);
        return commentService.getCommentsAdmin(userId, eventId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCommentByIdAdmin(@PathVariable(value = "commentId") Long commentId) {
        log.info("Удаление комментария по id = {}", commentId);
        commentService.deleteCommentByIdAdmin(commentId);
    }

    @PatchMapping("/{commentId}")
    @Validated(Marker.OnUpdate.class)
    public CommentDto updateCommentAdmin(@PathVariable(value = "commentId") Long commentId,
                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("Обновление комментария: {} по id = {}", commentDto, commentId);
        return commentService.updateCommentAdmin(commentId, commentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentByIdAdmin(@PathVariable(value = "commentId") Long commentId) {
        log.info("Получение комментария по id = {}", commentId);
        return commentService.getCommentByIdAdmin(commentId);
    }
}