package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.enums.StateActionEvent;
import ru.practicum.events.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventDto {

    @Size(max = 2000, min = 20)
    String annotation;

    Long category;

    @Size(max = 7000, min = 20)
    String description;

    @Future
    @JsonFormat(pattern = PATTERN_DATE)
    LocalDateTime eventDate;

    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionEvent stateAction;

    @Size(max = 120, min = 3)
    String title;
}