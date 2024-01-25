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
public class CommentControllerAdmin {

    private final CommentService commentService;
    static final String COMMENT_PATH = "/{commentId}";

    @PatchMapping(COMMENT_PATH)
    public CommentDto updateCommentByAdmin(@PathVariable Long commentId, @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Обновление комментария {} с id = {}", newCommentDto, commentId);
        return commentService.updateCommentByAdmin(commentId, newCommentDto);
    }


    @DeleteMapping(COMMENT_PATH)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Удаление комментария с id = {}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }


}
