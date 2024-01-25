package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.SortEvents;
import ru.practicum.events.enums.StateActionEvent;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidateDateException;
import ru.practicum.exceptions.ValidateException;
import ru.practicum.requests.EventRequestStatus;
import ru.practicum.requests.model.RequestShort;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;
import ru.practicum.util.PaginationSetup;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.events.enums.EventState.*;
import static ru.practicum.events.enums.SortEvents.EVENT_DATE;
import static ru.practicum.events.enums.SortEvents.VIEWS;
import static ru.practicum.events.mapper.EventMapper.mapToEventFullDto;
import static ru.practicum.events.mapper.EventMapper.mapToNewEvent;
import static ru.practicum.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        validDate(eventDto.getEventDate());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        Category category = getCategoryForEvent(eventDto.getCategory());
        Event event = eventRepository.save(mapToNewEvent(eventDto, user, category));
        log.info("Сохранение {}", event);
        return mapToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, int from, int size) {
        log.info("Получение");
        return eventRepository.findAllWithInitiatorByInitiator_Id(userId, new PaginationSetup(from, size,
                        Sort.unsorted())).stream().map(EventMapper::mapToEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = getEventByIdAndInitiatorId(eventId, userId);
        EventFullDto dto = mapToEventFullDto(event);
        dto.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(eventId));
        return dto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventById(Long userId, Long eventId, UpdateEventDto eventDto) {
        Event event = getEventByIdAndInitiatorId(eventId, userId);
        if (event.getState() == PUBLISHED || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Возможно изменять только мероприятия в статусе CANCELED или PENDING. " +
                    "Дата и время, на которые запланировано мероприятие, не могут быть раньше, " +
                    "чем через два часа с текущего момента");
        }
        updateEvent(event, eventDto);
        log.info("Обновление {}", event);
        return mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getAllEventsAdmin(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Integer from,
                                                Integer size) {
        log.info("Получение");
        validDateParam(rangeStart, rangeEnd);
        PageRequest pageable = new PaginationSetup(from, size, Sort.unsorted());
        List<Event> events = eventRepository.findAllForAdmin(users, states, categories, getRangeStart(rangeStart), getRangeEnd(rangeEnd),
                pageable);
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        Set<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> requestsList = requestRepository.findByEventIdInAndStatus(
                        eventIds,
                        EventRequestStatus.CONFIRMED).stream().collect(Collectors.toMap(RequestShort::getId, RequestShort::getCountRequest));
        for (Event event : events) {
            EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
            if (requestsList.get(event.getId()) != null) {
                eventFullDto.setConfirmedRequests(requestsList.get(event.getId()));
            }
            eventFullDtos.add(eventFullDto);
        }
        return eventFullDtos;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByIdAdmin(Long eventId, UpdateEventDto eventDto) {
        Event event = getEventById(eventId);
        updateEventAdmin(event, eventDto);
        log.info("Обновление {}", event);
        event = eventRepository.save(event);
        log.info("Сохранение {}", event);
        EventFullDto dto = mapToEventFullDto(event);
        dto.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(eventId));
        return dto;

    }

    @Override
    public EventFullDto getEventByIdPublic(Long id, String url, String ip) {
        log.info("Получение по id = {}", id);
        Event event = getEventById(id);
        log.info("Получение по id = {}", event);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + id + " не найдено");
        }
        statsClient.saveStats(APP, url, ip, LocalDateTime.now());
        EventFullDto fullDto = mapToEventFullDto(event);
        List<String> uris = List.of("/events/" + event.getId());
        List<ViewStats> views = statsClient.getStats(START_DATE, END_DATE, uris, null).getBody();
        if (views != null) {
            fullDto.setViews(views.size());
        }
        return fullDto;
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               SortEvents sort,
                                               Integer from,
                                               Integer size,
                                               String url, String ip) {
        log.info("Получение");
        validDateParam(rangeStart, rangeEnd);
        String sorting;
        if (sort.equals(EVENT_DATE)) {
            sorting = "eventDate";
        } else if (sort.equals(VIEWS)) {
            sorting = "views";
        } else {
            sorting = "id";
        }
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(sorting));
        final EventState state = PUBLISHED;
        List<Event> events = eventRepository.getEventsSort(text, EventState.PUBLISHED, categories, paid, getRangeStart(rangeStart), pageRequest);
        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));
        List<RequestShort> requestList = requestRepository.findByEventIdInAndStatus(
                eventsParticipantLimit.keySet(),
                EventRequestStatus.CONFIRMED);
        Map<Long, Long> requestsCountByIdEvent = requestList.stream()
                .collect(Collectors.toMap(RequestShort::getId, RequestShort::getCountRequest));
        if (onlyAvailable) {
            events.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > requestsCountByIdEvent.get(eventShort.getId())))
                    .collect(Collectors.toList());
        }
        if (rangeEnd != null) {
            events = getEventsBeforeRangeEnd(events, rangeEnd);
        }
        List<EventShortDto> result = events.stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
        saveViewInEvent(result);
        statsClient.saveStats(APP, url, ip, LocalDateTime.now());
        log.info("Сохранение статистики");
        if (sort.equals(VIEWS)) {
            return result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        return result;
    }

    private void validDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateDateException("Field: eventDate. Error: должно содержать дату, которая еще не наступила." +
                    " Value: " + eventDate);
        }
    }

    private Category getCategoryForEvent(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена"));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено"));
    }

    private Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private void updateEvent(Event event, UpdateEventDto eventDto) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getCategory() != null) {
            Category category = getCategoryForEvent(eventDto.getCategory());
            event.setCategory(category);
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            validDate(eventDto.getEventDate());
            event.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateActionEvent.CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
            if (eventDto.getStateAction().equals(StateActionEvent.SEND_TO_REVIEW)) {
                event.setState(PENDING);
            }
        }
        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }
    }

    private void updateEventAdmin(Event event, UpdateEventDto eventDto) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getCategory() != null) {
            Category category = getCategoryForEvent(eventDto.getCategory());
            event.setCategory(category);
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getStateAction() != null) {
            if (event.getState().equals(PENDING)) {
                if (eventDto.getStateAction().equals(StateActionEvent.REJECT_EVENT)) {
                    event.setState(CANCELED);
                }
                if (eventDto.getStateAction().equals(StateActionEvent.PUBLISH_EVENT)) {
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
            } else {
                throw new ValidateException("Неверный статус мероприятия " + event.getState() +
                        ". Невозможно его изменить или отменить");
            }
        }
        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }
        if (eventDto.getEventDate() != null && event.getState().equals(PUBLISHED)) {
            if (eventDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                event.setEventDate(eventDto.getEventDate());
            } else {
                throw new ValidateDateException("Событие не может быть изменено раньше, чем через час после публикации.");
            }
        }
    }

    private void validDateParam(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidateDateException("Дата начала диапазона не может быть позже даты окончания диапазона.");
            }
        }
    }

    private Long getId(String url) {
        String[] uri = url.split("/");
        return Long.valueOf(uri[uri.length - 1]);
    }

    private void saveViewInEvent(List<EventShortDto> result) {
        List<String> uris = result
                .stream()
                .map(eventShortDto -> "/events/" + eventShortDto.getId())
                .collect(Collectors.toList());
        log.info("Получение");
        List<ViewStats> views = statsClient.getStats(START_DATE, END_DATE, uris, null).getBody();
        if (views != null) {
            Map<Long, Long> mapIdHits = new HashMap<>();
            views.forEach(viewStats -> mapIdHits.put(getId(viewStats.getUri()), viewStats.getHits()));
            result.forEach(eventShortDto -> {
                if (mapIdHits.containsKey(eventShortDto.getId())) {
                    eventShortDto.setViews(mapIdHits.get(eventShortDto.getId()));
                }
            });
        }
    }

    private LocalDateTime getRangeStart(LocalDateTime rangeStart) {
        if (rangeStart == null) {
            return LocalDateTime.now();
        }
        return rangeStart;
    }

    private LocalDateTime getRangeEnd(LocalDateTime rangeEnd) {
        if (rangeEnd == null) {
            return LocalDateTime.now().plusDays(1000);
        }
        return rangeEnd;
    }

    private List<Event> getEventsBeforeRangeEnd(List<Event> events, LocalDateTime rangeEnd) {
        return events.stream().filter(event -> event.getEventDate().isBefore(rangeEnd)).collect(Collectors.toList());
    }

}
