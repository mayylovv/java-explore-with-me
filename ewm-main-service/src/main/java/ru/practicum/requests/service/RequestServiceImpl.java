package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.requests.dto.RequestStatusParticipation;
import ru.practicum.requests.dto.RequestStatusConfirm;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ValidateException;
import ru.practicum.exception.NotFoundException;
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

import static ru.practicum.events.enums.EventStatus.PUBLISHED;
import static ru.practicum.requests.enums.RequestStatus.*;
import static ru.practicum.requests.mapper.RequestMapper.mapToNewParticipationRequest;
import static ru.practicum.requests.mapper.RequestMapper.mapToParticipationRequestDto;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;


    @Transactional
    @Override
    public RequestDto createRequest(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        log.info("Получение по id = {}", event);

        if (!event.getState().equals(PUBLISHED)) {
            throw new ValidateException("Нельзя участвовать в несуществующем событии.");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new ValidateException("Инициатор события не может подать запрос на участие в своём событии");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidateException("Исчерпан лимит заявок на участие в мероприятии.");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidateException("Заявка на участие уже была подана");
        }

        Request newRequest = requestRepository.save(mapToNewParticipationRequest(event, user));
        log.info("Сохранение {}", newRequest);

        if (newRequest.getStatus() == CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return mapToParticipationRequestDto(newRequest);
    }

    @Override
    public List<RequestDto> getRequestByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        log.info("Получение");
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequest(Long userId, Long eventId) {
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
                                                         RequestStatusParticipation dtoRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено."));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ValidateException("Лимит заявок равен 0 для мероприятия с id = " + eventId +
                    " или отключена премодерация заявокю");
        }

        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId, dtoRequest.getRequestIds());

        boolean isStatusPending = requests.stream().anyMatch(request -> !request.getStatus().equals(PENDING));
        if (isStatusPending) {
            throw new ValidateException("Статус можно изменить только для запросов, находящихся в состоянии PENDING");
        }
        log.info("Обновление статуса");
        switch (dtoRequest.getStatus()) {
            case CONFIRMED:
                return saveConfirmedStatus(requests, event);
            case REJECTED:
                return saveRejectedStatus(requests, event);
            default:
                throw new ValidateException("Неизвестный статус: " + dtoRequest.getStatus());
        }
    }

    @Transactional
    @Override
    public RequestDto updateStatusRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Заявка с id = " + requestId + " не найдена "));
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

    private RequestStatusConfirm saveConfirmedStatus(List<Request> requests, Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidateException("Исчерпан лимит заявок на участие в мероприятии.");
        }
        int limitUpdate = event.getParticipantLimit() - event.getConfirmedRequests();

        if (requests.size() <= limitUpdate) {
            requests.forEach(request -> request.setStatus(CONFIRMED));
            requestRepository.saveAll(requests);
            event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
            eventRepository.save(event);
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
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
        eventRepository.save(event);
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