package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import ru.practicum.events.EventState;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private Long id; // Идентификатор;
    private String title; // Заголовок;
    private String annotation; // Краткое описание;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss");
    private Location location; // Широта и долгота места проведения события;
    private boolean paid; // Нужно ли оплачивать участие;
    private int participantLimit; // Ограничение на количество участников;
    private int confirmedRequests; // Количество одобренных заявок на участие в данном событии;
    private long views; // Количество просмотров события.
    private EventState state; // Список состояний жизненного цикла события;

    @JsonUnwrapped
    AdditionalEventInformation eventInformation;


}