package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable(value = "userId") Long userId,
                                    @RequestParam(value = "eventId") Long eventId) {
        log.info("Создание запроса по userId = {} и eventId = {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public Collection<RequestDto> getRequest(@PathVariable(value = "userId") Long userId) {
        log.info("Получение запроса по userId = {}", userId);
        return requestService.getRequestByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto updateParticipationRequestStatusToCancel(@PathVariable(value = "userId") Long userId,
                                                               @PathVariable(value = "requestId") Long requestId) {
        log.info("Обновление статуса запроса на cancel. Параметры: userId = {} и requestId = {}", userId, requestId);
        return requestService.updateStatusParticipationRequest(userId, requestId);
    }
}
