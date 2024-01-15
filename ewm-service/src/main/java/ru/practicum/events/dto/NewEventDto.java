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
    private String annotation; // Краткое описание;

    @NotNull(message = "Field: category. Error: must not be blank. Value: null")
    private Long category; // id категории к которой относится событие;

    @NotBlank
    @Size(max = 7000, min = 20)
    private String description; // Полное описание события;

    @NotNull
    @Future
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss");

    private Location location; // Широта и долгота места проведения события;
    private Boolean paid = false; // Нужно ли оплачивать участие в событии. Default: false;

    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения. Default: 0;
    private Integer participantLimit = 0;

    /* Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут ожидать подтверждения инициатором события.
    Если false - то будут подтверждаться автоматически. Default: true*/
    private Boolean requestModeration = true;

    @NotBlank
    @Size(max = 120, min = 3)
    private String title; // Заголовок события;
}