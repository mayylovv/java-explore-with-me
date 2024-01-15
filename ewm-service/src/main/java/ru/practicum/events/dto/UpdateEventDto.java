package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.events.StateActionEvent;
import ru.practicum.events.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDto {
    //Данные для изменения информации о событии. Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.

    @Size(max = 2000, min = 20)
    private String annotation; // Краткое описание;
    private Long category; // id категории к которой относится событие;

    @Size(max = 7000, min = 20)
    private String description; // Полное описание события;

    @Future
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss");
    private Location location; // Широта и долгота места проведения события;
    private Boolean paid; // Нужно ли оплачивать участие в событии.
    private Integer participantLimit; // Ограничение на количество участников.
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие.
    private StateActionEvent stateAction; // Изменение состояния события;

    @Size(max = 120, min = 3)
    private String title; // Заголовок события;
}