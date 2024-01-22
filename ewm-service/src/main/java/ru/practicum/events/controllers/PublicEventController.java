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
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        SortEvents sortParam = SortEvents.from(sort)
                .orElseThrow(() -> new ValidationException("Ошибка сортировки: " + sort));
        log.info("Получить события с параметрами: текст {}, категория {}, платные {}, с {}, по {}, " +
                        "в наличии {}, сортировка {}, от {}, размер {}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        log.info("IP клиента: {}", request.getRemoteAddr());
        log.info("FQDN: {}", request.getRequestURI());
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sortParam,
                from, size, url, ip);
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getPublicEvent(@PathVariable(value = "id") Long id, HttpServletRequest request) {
        log.info("Получение события по ID {}", id);
        log.info("IP клиента: {}", request.getRemoteAddr());
        log.info("FQDN: {}", request.getRequestURI());

        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        return eventService.getEventByIdPublic(id, url, ip);
    }
}
