package ru.practicum.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<EventShortDto> getUserEvents(@PathVariable(value = "userId") Long userId,
                                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET '/users/{userId}/events'. Запрос на получение событий пользователя с id {}", userId);
        Collection<EventShortDto> response = eventService.getUserEvents(userId, from, size);
        log.info("GET '/users/{userId}/events'. Ответ, события пользователя с id {}", userId);
        return response;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable(value = "userId") Long userId, @Valid @RequestBody NewEventDto eventDto) {
        log.info("POST '/users/{userId}/events'. Запрос на сохранение события пользователя с id {}", userId);
        EventFullDto response = eventService.saveEventByIdUser(userId, eventDto);
        log.info("POST '/users/{userId}/events'. Ответ, событие пользователя с id {} сохранено", userId);
        return response;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable(value = "userId") Long userId, @PathVariable(value = "eventId") Long eventId) {
        log.info("GET '/users/{userId}/events/{eventId}'. Запрос на получение события с id {} пользователя с id {}", eventId, userId);
        EventFullDto response = eventService.getEvent(userId, eventId);
        log.info("GET '/users/{userId}/events/{eventId}'. Ответ, событие {}", response);
        return response;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventDto eventDto) {
        log.info("PATCH '/users/{userId}/events/{eventId}'. Запрос на обновление события с id {} пользователя с id {}", eventId, userId);
        EventFullDto response = eventService.updateEventById(userId, eventId, eventDto);
        log.info("PATCH '/users/{userId}/events/{eventId}'. Ответ, обновление события с id {} успешно завершено {}", eventId, response);
        return response;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<ParticipationRequestDto> getParticipationRequestByEvent(@PathVariable(value = "userId") Long userId,
                                                                              @PathVariable(value = "eventId") Long eventId) {
        log.info("GET '/users/{userId}/events/{eventId}/requests'. Запрос на участии в событии с id {} пользователя {}", eventId, userId);
        Collection<ParticipationRequestDto> response = requestService.getParticipationRequest(userId,eventId);
        log.info("GET '/users/{userId}/events/{eventId}/requests'. Ответ, информация о запросах участия {}", response);
        return response;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable(value = "userId") Long userId,
                                                                   @PathVariable(value = "eventId") Long eventId,
                                                                   @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("PATCH '/users/{userId}/events/{eventId}/requests'. Запрос на обновление заявки {} на участие пользователя {}", eventId, userId);
        EventRequestStatusUpdateResult response = requestService.updateEventRequestStatus(userId,eventId, updateRequest);
        log.info("PATCH '/users/{userId}/events/{eventId}/requests'. Ответ, заявка {} успешно обновлена", eventId);
        return response;
    }
}
