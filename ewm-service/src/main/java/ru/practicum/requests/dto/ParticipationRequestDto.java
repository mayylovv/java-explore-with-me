package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.requests.EventRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ParticipationRequestDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    private LocalDateTime created; // Дата и время создания заявки;
    private Long event; // Идентификатор события
    private Long id; // Идентификатор заявки
    private Long requester; // Идентификатор пользователя, отправившего заявку
    private EventRequestStatus status; // Статус заявки.
}