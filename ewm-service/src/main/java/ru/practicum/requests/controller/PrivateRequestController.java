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
        log.info("Get participation request by user Id {}", userId);
        return requestService.getParticipationRequestByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto saveParticipationRequest(@PathVariable(value = "userId") Long userId,
                                                            @RequestParam(value = "eventId") Long eventId) {
        log.info("Creating participation request by user Id {} and event Id {}", userId, eventId);
        return requestService.saveParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateParticipationRequestStatusToCancel(
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "requestId") Long requestId) {
        log.info("Updating participation request status to cancel. Parameters: userId {} and request Id {}", userId,
                requestId);
        return requestService.updateStatusParticipationRequest(userId, requestId);
    }
}