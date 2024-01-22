package ru.practicum.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(User user, Event event, NewCommentDto commentDto) {
        return Comment
                .builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .author(user)
                .event(event)
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .eventId(comment.getEvent().getId())
                .build();
    }
}
