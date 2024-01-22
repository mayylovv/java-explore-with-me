package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public Collection<ParticipationRequestDto> getParticipationRequest(@PathVariable(value = "userId") Long userId) {
        log.info("GET '/users/{userId}/requests'. Запрос на получение списка всех участий в чужих событиях пользователя {} ", userId);
        Collection<ParticipationRequestDto> response = requestService.getParticipationRequestByUserId(userId);
        log.info("GET '/users/{userId}/requests'. Ответ, список событий {} ", response);
        return response;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto saveParticipationRequest(@PathVariable(value = "userId") Long userId,
                                                            @RequestParam(value = "eventId") Long eventId) {
        log.info("POST '/users/{userId}/requests'. Добавление запроса на участие в событии {} пользователя {} ", eventId, userId);
        ParticipationRequestDto response = requestService.saveParticipationRequest(userId, eventId);
        log.info("POST '/users/{userId}/requests'. Ответ, запрос успешно сохранен {} ", response);
        return response;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateParticipationRequestStatusToCancel(
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "requestId") Long requestId) {
        log.info("PATCH '/users/{userId}/requests/{requestId}/cancel'. Отмена запроса на участие в событии {} ", requestId);
        ParticipationRequestDto response = requestService.updateStatusParticipationRequest(userId, requestId);
        log.info("PATCH '/users/{userId}/requests/{requestId}/cancel'. Ответ, событие отменено {} ", response);
        return response;
    }
}
