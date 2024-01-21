package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.util.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    private Long id; // уникальный идентификатор комментария;
    @NotBlank
    @Size(min = 2, max = 2000)
    private String text; // содержимое комментария;
    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    private Long authorId; // автор комментария;
    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    private Long eventId; // уникальный идентификатор события;
    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    private LocalDateTime created; // дата создания комментария.
}