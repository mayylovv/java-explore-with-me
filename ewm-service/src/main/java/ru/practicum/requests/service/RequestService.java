package ru.practicum.requests.service;

import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                  EventRequestStatusUpdateRequest dtoRequest);

    List<ParticipationRequestDto> getParticipationRequestByUserId(Long userId);

    ParticipationRequestDto saveParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto updateStatusParticipationRequest(Long userId, Long requestId);
}