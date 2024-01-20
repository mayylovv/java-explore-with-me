package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.util.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    Long id;

    @NotBlank
    @Size(min = 2, max = 2000)
    String text;

    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    Long authorId;

    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    Long eventId;;

    @Null(groups = {Marker.OnCreate.class,Marker.OnUpdate.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    LocalDateTime created;

}