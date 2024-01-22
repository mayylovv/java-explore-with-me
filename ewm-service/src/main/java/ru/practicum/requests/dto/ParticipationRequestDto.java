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
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private EventRequestStatus status;
}
