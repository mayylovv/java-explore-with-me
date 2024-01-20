package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdditionalInformation {

    String description;
    CategoryDto category;
    boolean requestModeration;
    UserShortDto initiator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    LocalDateTime publishedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    LocalDateTime createdOn;
    List<CommentDto> comments;

}
