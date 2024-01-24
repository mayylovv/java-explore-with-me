package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank
    @Size(max = 2000, min = 20)
    String annotation;

    @NotNull(message = "Field: category. Error: must not be blank. Value: null")
    Long category;

    @NotBlank
    @Size(max = 7000, min = 20)
    String description;

    @NotNull
    @Future
    @JsonFormat(pattern = PATTERN_DATE)
    LocalDateTime eventDate;

    Location location;
    Boolean paid = false;
    Integer participantLimit = 0;
    Boolean requestModeration = true;

    @NotBlank
    @Size(max = 120, min = 3)
    String title;
}
