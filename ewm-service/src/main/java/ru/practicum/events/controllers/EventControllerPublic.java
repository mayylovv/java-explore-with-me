package ru.practicum.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.enums.SortEvents;
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

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventControllerPublic {

    private final EventService eventService;

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getEventByIdPublic(@PathVariable(value = "id") Long id, HttpServletRequest request) {
        log.info("Получение события по id = {} (Public)", id);
        return eventService.getEventByIdPublic(id, request);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                     @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size,
                                                     HttpServletRequest request) {
        SortEvents sortParam = SortEvents.from(sort)
                .orElseThrow(() -> new ValidationException("Неизвестный параметр: " + sort));
        log.info("Получение события с параметрами: text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, " +
                        "onlyAvailable = {}, sort = {}, from = {}, size = {} (Public)", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sortParam, from, size, request);
    }
}
