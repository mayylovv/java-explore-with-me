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
    public Collection<EventFullDto> getEvents(
            // Список id пользователей, чьи события нужно найти
            @RequestParam(required = false) List<Long> users,

            // список состояний в которых находятся искомые события
            @RequestParam(required = false) List<EventState> states,

            // список id категорий в которых будет вестись поиск
            @RequestParam(required = false) List<Long> categories,

            // дата и время не раньше которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,

            // дата и время не позже которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get events users {} with parameters: states {}, categories {}, rangeStart {}, rangeEnd {}, from {}, " +
                "size {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAllEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEventAdmin(@PathVariable(value = "eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventDto eventDto) {
        log.info("Updating event {} by event Id {}", eventDto, eventId);
        return eventService.updateEventByIdAdmin(eventId, eventDto);
    }
}