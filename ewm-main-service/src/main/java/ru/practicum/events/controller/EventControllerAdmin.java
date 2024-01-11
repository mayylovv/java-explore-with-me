package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.enums.EventStatus;
import ru.practicum.events.dto.FullEventDto;
import ru.practicum.events.dto.UpdateEventDto;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdmin {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public FullEventDto updateEvent(@PathVariable(value = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("Обновление события {} по id = {}", updateEventDto, eventId);
        return eventService.updateEventByIdForAdmin(eventId, updateEventDto);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<FullEventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<EventStatus> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение события по параметрам");
        return eventService.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}