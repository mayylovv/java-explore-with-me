package ru.practicum.comments.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

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

    public Comment toComment(User user, Event event, NewCommentDto commentDto) {
        return Comment
                .builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .author(user)
                .event(event)
                .build();
    }
}
