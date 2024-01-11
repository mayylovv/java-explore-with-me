package ru.practicum.requests.service;

import ru.practicum.requests.dto.RequestStatusParticipation;
import ru.practicum.requests.dto.RequestStatusСonfirmation;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getRequestByUserId(Long userId);

    List<RequestDto> getRequest(Long userId, Long eventId);

    RequestStatusСonfirmation updateEventRequestStatus(Long userId, Long eventId, RequestStatusParticipation dtoRequest);

    RequestDto updateStatusRequest(Long userId, Long requestId);
}