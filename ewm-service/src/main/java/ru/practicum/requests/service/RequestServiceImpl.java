package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.requests.dto.RequestStatusParticipation;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ValidateException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.util.Messages.*;
import static ru.practicum.events.enums.EventState.PUBLISHED;
import static ru.practicum.requests.EventRequestStatus.*;
import static ru.practicum.requests.dto.ParticipationRequestMapper.mapToNewParticipationRequest;
import static ru.practicum.requests.dto.ParticipationRequestMapper.mapToParticipationRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId) {
        log.info(GET_MODELS.getMessage());
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isPresent()) {
            return requestRepository.findAllByEventId(eventId).stream()
                    .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                   RequestStatusParticipation dtoRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId));
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ValidateException("Подтверждение не требуется");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(eventId,
                dtoRequest.getRequestIds());
        validRequestStatus(requests);
        log.info(UPDATE_STATUS.getMessage());
        switch (dtoRequest.getStatus()) {
            case CONFIRMED:
                return saveConfirmedStatus(requests, event);
            case REJECTED:
                return saveRejectedStatus(requests, event);
            default:
                throw new ValidateException("Неизвестный state: " + dtoRequest.getStatus());
        }
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        log.info(GET_MODELS.getMessage());
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto saveParticipationRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId));
        log.info(GET_MODEL_BY_ID.getMessage(), event);
        if (userId.equals(event.getInitiator().getId())) {
            throw new ValidateException("Инициатор не может добавить заявку");
        }
        if (!event.getState().equals(PUBLISHED)) {
            throw new ValidateException("Вы не можете участвовать в неопубликованном мероприятии");
        }
        validParticipantLimit(event);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidateException("Вы не можете добавить повторный запрос");
        }
        ParticipationRequest newRequest = requestRepository.save(mapToNewParticipationRequest(event, user));
        log.info(SAVE_MODEL.getMessage(), newRequest);

        return mapToParticipationRequestDto(newRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto updateStatusParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id = " + requestId));
        request.setStatus(CANCELED);
        log.info(UPDATE_MODEL.getMessage(), request);
        return mapToParticipationRequestDto(requestRepository.save(request));
    }

    private void validParticipantLimit(Event event) {
        int confirmedRequests = requestRepository.getConfirmedRequestsByEventId(event.getId());
        if (event.getParticipantLimit() > 0 && confirmedRequests == (event.getParticipantLimit())) {
            throw new ValidateException("В мероприятии исчерпан лимит заявок на участие");
        }
    }

    private void validRequestStatus(List<ParticipationRequest> requests) {
        boolean isStatusPending = requests.stream()
                .anyMatch(request -> !request.getStatus().equals(PENDING));

        if (isStatusPending) {
            throw new ValidateException("Можно изменить только для запросов в статусе PENDING");
        }
    }

    private EventRequestStatusUpdateResult saveConfirmedStatus(List<ParticipationRequest> requests, Event event) {
        validParticipantLimit(event);

        int limitUpdate = event.getParticipantLimit() - requestRepository.getConfirmedRequestsByEventId(event.getId());
        if (requests.size() <= limitUpdate) {
            requests.forEach(request -> request.setStatus(CONFIRMED));
            requestRepository.saveAll(requests);

            return new EventRequestStatusUpdateResult(requests
                    .stream()
                    .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                    .collect(Collectors.toList()), List.of());
        }
        List<ParticipationRequest> confirmedRequests = requests.stream()
                .limit(limitUpdate)
                .collect(Collectors.toList());

        List<ParticipationRequest> rejectedRequests = requests.stream()
                .filter(element -> !confirmedRequests.contains(element))
                .peek(request -> request.setStatus(REJECTED))
                .collect(Collectors.toList());

        confirmedRequests.forEach(request -> request.setStatus(CONFIRMED));

        requestRepository.saveAll(Stream.of(confirmedRequests, rejectedRequests)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return new EventRequestStatusUpdateResult(confirmedRequests
                .stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList()),
                rejectedRequests.stream()
                        .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                        .collect(Collectors.toList()));
    }

    private EventRequestStatusUpdateResult saveRejectedStatus(List<ParticipationRequest> requests, Event event) {
        requests.forEach(request -> request.setStatus(REJECTED));
        requestRepository.saveAll(requests);
        List<ParticipationRequestDto> rejectedRequests = requests
                .stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(List.of(), rejectedRequests);
    }
}
