package ru.practicum.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.EventState;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventDto;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.util.Constants.PATTERN_DATE;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<EventState> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET '/admin/events'. Запрос на получение событий пользователя {} с параметрами: состояние {}, " +
                "категории {}, с {}, по {}, из {}, размер {}", users, states, categories, rangeStart, rangeEnd, from, size);
        Collection<EventFullDto> response = eventService.getAllEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("GET '/admin/events'. Ответ, события пользователя c id {} {}", users, response);
        return response;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEventAdmin(@PathVariable(value = "eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventDto eventDto) {
        log.info("PATCH '/admin/events/{eventId}'. Запрос на обновление события с id {}", eventId);
        EventFullDto response = eventService.updateEventByIdAdmin(eventId, eventDto);
        log.info("PATCH '/admin/events/{eventId}'. Ответ, событие с id {} успешно обновлено {}", eventId, response);
        return response;
    }
}
