package ru.practicum.comments.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private Long eventId;
    private LocalDateTime created;
}
