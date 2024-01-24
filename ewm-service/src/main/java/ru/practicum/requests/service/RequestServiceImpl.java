package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.requests.dto.RequestStatusParticipation;
import ru.practicum.requests.dto.RequestStatusConfirm;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ValidateException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.events.enums.EventState.PUBLISHED;
import static ru.practicum.requests.EventRequestStatus.*;
import static ru.practicum.requests.mapper.RequestMapper.mapToNewParticipationRequest;
import static ru.practicum.requests.mapper.RequestMapper.mapToParticipationRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
        log.info("Получение по id = {}", event);
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
        Request newRequest = requestRepository.save(mapToNewParticipationRequest(event, user));
        log.info("Сохранение {}", newRequest);
        return mapToParticipationRequestDto(newRequest);
    }

    @Override
    public List<RequestDto> getParticipationRequest(Long userId, Long eventId) {
        log.info("Получение");
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isPresent()) {
            return requestRepository.findAllByEventId(eventId).stream()
                    .map(RequestMapper::mapToParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    @Override
    public RequestStatusConfirm updateEventRequestStatus(Long userId, Long eventId,
                                                         RequestStatusParticipation requestStatusParticipation) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ValidateException("Подтверждение не требуется");
        }
        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId,
                requestStatusParticipation.getRequestIds());
        validRequestStatus(requests);
        log.info("Обновление");
        switch (requestStatusParticipation.getStatus()) {
            case CONFIRMED:
                return saveConfirmedStatus(requests, event);
            case REJECTED:
                return saveRejectedStatus(requests, event);
            default:
                throw new ValidateException("Неизвестный state: " + requestStatusParticipation.getStatus());
        }
    }

    @Override
    public List<RequestDto> getRequestByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        log.info("Получение");
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RequestDto updateStatusParticipationRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
        request.setStatus(CANCELED);
        log.info("Обновление {}", request);
        return mapToParticipationRequestDto(requestRepository.save(request));
    }

    private RequestStatusConfirm saveRejectedStatus(List<Request> requests, Event event) {
        requests.forEach(request -> request.setStatus(REJECTED));
        requestRepository.saveAll(requests);
        List<RequestDto> rejectedRequests = requests
                .stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
        return new RequestStatusConfirm(List.of(), rejectedRequests);
    }

    private void validRequestStatus(List<Request> requests) {
        boolean isStatusPending = requests.stream()
                .anyMatch(request -> !request.getStatus().equals(PENDING));
        if (isStatusPending) {
            throw new ValidateException("Можно изменить только для запросов в статусе PENDING");
        }
    }

    private void validParticipantLimit(Event event) {
        int confirmedRequests = requestRepository.getConfirmedRequestsByEventId(event.getId());
        if (event.getParticipantLimit() > 0 && confirmedRequests == (event.getParticipantLimit())) {
            throw new ValidateException("Исчерпан лимит заявок на участие");
        }
    }

    private RequestStatusConfirm saveConfirmedStatus(List<Request> requests, Event event) {
        validParticipantLimit(event);
        int limitUpdate = event.getParticipantLimit() - requestRepository.getConfirmedRequestsByEventId(event.getId());
        if (requests.size() <= limitUpdate) {
            requests.forEach(request -> request.setStatus(CONFIRMED));
            requestRepository.saveAll(requests);
            return new RequestStatusConfirm(requests
                    .stream()
                    .map(RequestMapper::mapToParticipationRequestDto)
                    .collect(Collectors.toList()), List.of());
        }
        List<Request> confirmedRequests = requests.stream()
                .limit(limitUpdate)
                .collect(Collectors.toList());
        List<Request> rejectedRequests = requests.stream()
                .filter(element -> !confirmedRequests.contains(element))
                .peek(request -> request.setStatus(REJECTED))
                .collect(Collectors.toList());
        confirmedRequests.forEach(request -> request.setStatus(CONFIRMED));
        requestRepository.saveAll(Stream.of(confirmedRequests, rejectedRequests)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return new RequestStatusConfirm(confirmedRequests
                .stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList()),
                rejectedRequests.stream()
                        .map(RequestMapper::mapToParticipationRequestDto)
                        .collect(Collectors.toList()));
    }
}
