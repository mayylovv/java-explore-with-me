package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {


    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByAdmin(@PathVariable Long commentId, @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("PATCH '/admin/comments'. Запрос на обновление комментария {} ", commentId);
        CommentDto response = commentService.updateCommentByAdmin(commentId, newCommentDto);
        log.info("PATCH '/admin/comments'. Ответ, комментарий обновлен: {}", response);
        return response;
    }


    @DeleteMapping("/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("DELETE '/admin/comments/{commentId}'. Запрос удаления комментария {}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }


}
