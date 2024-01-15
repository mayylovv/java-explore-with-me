package ru.practicum.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.SortEvents;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.util.Constants.PATTERN_DATE;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<EventShortDto> getEventsPublic(
            // текст для поиска в содержимом аннотации и подробном описании события
            @RequestParam(required = false) String text,

            // список идентификаторов категорий в которых будет вестись поиск
            @RequestParam(required = false) List<Long> categories,

            // поиск только платных/бесплатных событий
            @RequestParam(required = false) Boolean paid,

            // дата и время не раньше которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,

            // дата и время не позже которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,

            // только события у которых не исчерпан лимит запросов на участие
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,

            // Вариант сортировки: по дате события или по количеству просмотров
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        SortEvents sortParam = SortEvents.from(sort)
                .orElseThrow(() -> new ValidationException("Unknown param sort: " + sort));

        log.info("Get events with parameters: text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, " +
                "onlyAvailable {}, sort {}, from {}, size {}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sortParam, from, size, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getEventByIdPublic(@PathVariable(value = "id") Long id,
                                           HttpServletRequest request) {
        log.info("Get event by Id {}", id);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventService.getEventByIdPublic(id, request);
    }
}
