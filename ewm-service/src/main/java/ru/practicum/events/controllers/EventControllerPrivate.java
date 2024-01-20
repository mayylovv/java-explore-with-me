package ru.practicum.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.RequestStatusParticipation;
import ru.practicum.requests.dto.RequestStatusConfirm;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerPrivate {

    private final RequestService requestService;
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(value = "userId") Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Создание события {} по userId = {}", eventDto, userId);
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping
    public Collection<EventShortDto> getEventsByUserId(@PathVariable(value = "userId") Long userId,
                                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получения события по userId = {} с параметрами: from = {} size = {}", userId, from, size);
        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "eventId") Long eventId) {
        log.info("Получения события по userId = {} и eventId = {}", userId, eventId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventDto eventDto) {
        log.info("Обновление события {} по userId = {} и eventId = {}", eventDto, userId, eventId);
        return eventService.updateEventById(userId, eventId, eventDto);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<RequestDto> getRequestByIdEvent(@PathVariable(value = "userId") Long userId,
                                                      @PathVariable(value = "eventId") Long eventId) {
        log.info("Получение запроса на участие в событии eventId = {} пользователя с userId = {} and ", eventId, userId);
        return requestService.getParticipationRequest(userId,eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusConfirm updateEventRequestStatus(@PathVariable(value = "userId") Long userId,
                                                         @PathVariable(value = "eventId") Long eventId,
                                                         @RequestBody RequestStatusParticipation updateRequest) {
        log.info("Обновление статуса запроса: userId = {}, eventId = {}", userId, eventId);
        return requestService.updateEventRequestStatus(userId,eventId, updateRequest);
    }
}