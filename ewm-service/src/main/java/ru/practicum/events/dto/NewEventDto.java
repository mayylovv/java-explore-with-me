package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.events.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(max = 2000, min = 20)
    private String annotation;
    @NotNull(message = "Ошибка, поле category не должно быть пустым")
    private Long category;
    @NotBlank
    @Size(max = 7000, min = 20)
    private String description;
    @NotNull
    @Future
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank
    @Size(max = 120, min = 3)
    private String title;
}
