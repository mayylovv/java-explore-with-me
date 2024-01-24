package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.requests.EventRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    EventRequestStatus status;
}
