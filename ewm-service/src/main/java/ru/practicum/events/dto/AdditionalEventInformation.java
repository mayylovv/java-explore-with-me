package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdditionalEventInformation {

    private String description; // Полное описание события;
    private CategoryDto category; // Категория;
    private boolean requestModeration; // Нужна ли пре-модерация заявок на участие, default: true;
    private UserShortDto initiator; // Инициатор;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss");

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    private LocalDateTime createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
}
