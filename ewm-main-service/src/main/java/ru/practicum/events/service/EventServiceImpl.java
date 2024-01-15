package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.events.enums.EventStatus;
import ru.practicum.events.enums.EventSort;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.util.PaginationSetup;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.enums.ActionEventStatus;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.exception.ValidateDateException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.events.enums.EventSort.EVENT_DATE;
import static ru.practicum.events.enums.EventSort.VIEWS;
import static ru.practicum.util.Constants.*;
import static ru.practicum.events.enums.EventStatus.*;
import static ru.practicum.events.mapper.EventMapper.mapToFullEventDto;
import static ru.practicum.events.mapper.EventMapper.mapToNewEvent;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;


    @Transactional
    @Override
    public FullEventDto createEventByUserId(Long userId, NewEventDto eventDto) {
        validDate(eventDto.getEventDate());
        User user = getUserById(userId);
        Category category = getCategoryById(eventDto.getCategory());
        Event event = eventRepository.save(mapToNewEvent(eventDto, user, category));
        log.info("Сохранение {}", event);
        return mapToFullEventDto(event);
    }

    @Override
    public List<ShortEventDto> getAllEventsByUserId(Long userId, int from, int size) {
        log.info("Получение");
        return eventRepository.findAllWithInitiatorByInitiatorId(userId, new PaginationSetup(from, size,
                        Sort.unsorted())).stream().map(EventMapper::mapToShortEventDto).collect(Collectors.toList());
    }

    @Override
    public FullEventDto getEventByUserId(Long userId, Long eventId) {
        Event event = getEventByEventIdAndUserId(eventId, userId);
        return mapToFullEventDto(event);
    }

    @Transactional
    @Override
    public FullEventDto updateEventById(Long userId, Long eventId, UpdateEventDto eventDto) {
        Event event = getEventByEventIdAndUserId(eventId, userId);
        if (event.getState() == PUBLISHED || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Возможно изменять только мероприятия в статусе CANCELED или PENDING. " +
                    "Дата и время, на которые запланировано мероприятие, не могут быть раньше, " +
                    "чем через два часа с текущего момента");
        }
        updateEvent(event, eventDto);
        log.info("Обновление {}", event);
        return mapToFullEventDto(eventRepository.save(event));
    }

    @Override
    public List<FullEventDto> getAllEventsForAdmin(List<Long> users,
                                                   List<EventStatus> states,
                                                   List<Long> categories,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Integer from,
                                                   Integer size) {
        log.info("Получение");
        validDateParam(rangeStart, rangeEnd);
        PageRequest pageable = new PaginationSetup(from, size, Sort.unsorted());
        List<Event> events = eventRepository.findAllForAdmin(users, states, categories, getRangeStart(rangeStart), pageable);
        if (rangeEnd != null) {
            events = events.stream().filter(event -> event.getEventDate().isBefore(rangeEnd)).collect(Collectors.toList());
        }
        return events.stream().map(EventMapper::mapToFullEventDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public FullEventDto updateEventByIdForAdmin(Long eventId, UpdateEventDto eventDto) {
        Event event = getEventById(eventId);
        updateEventAdmin(event, eventDto);
        log.info("Обновление {}", event);
        event = eventRepository.save(event);
        log.info("Сохранение {}", event);
        return mapToFullEventDto(event);
    }

    @Override
    public List<ShortEventDto> getEventsForPublic(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  EventSort sort,
                                                  Integer from,
                                                  Integer size,
                                                  HttpServletRequest request) {
        log.info("Получение");
        validDateParam(rangeStart, rangeEnd);
        PaginationSetup pageable = new PaginationSetup(from, size, Sort.unsorted());
        final EventStatus state = PUBLISHED;
        List<Event> events;

        if (sort.equals(EVENT_DATE)) {
            pageable = new PaginationSetup(from, size, Sort.by("eventDate"));
        }

        if (onlyAvailable) {
            events = eventRepository.findAllPublishStateOnlyNotAvailable(state, getRangeStart(rangeStart), categories,
                    paid, text, pageable);
        } else {
            events = eventRepository.findAllPublishStateOnlyAvailable(state, getRangeStart(rangeStart), categories,
                    paid, text, pageable);
        }

        if (rangeEnd != null) {
            events = events.stream().filter(event -> event.getEventDate().isBefore(rangeEnd)).collect(Collectors.toList());
        }

        List<ShortEventDto> result = events.stream().map(EventMapper::mapToShortEventDto).collect(Collectors.toList());
        saveViewInEvent(result);
        statsClient.saveStats(APP, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        log.info("Сохранение статистики");

        if (sort.equals(VIEWS)) {
            return result.stream().sorted(Comparator.comparingLong(ShortEventDto::getViews)).collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public FullEventDto getEventByIdForPublic(Long id, HttpServletRequest request) {
        log.info("Получение по id = {}", id);
        Event event = getEventById(id);
        log.info("Получение по id = {}", event);

        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + id + " не опубликовано");
        }

        FullEventDto fullDto = mapToFullEventDto(event);

        List<String> uris = List.of("/events/" + event.getId());
        List<ViewStats> views = statsClient.getStats(START_DATE, END_DATE, uris, null).getBody();

        if (views != null) {
            fullDto.setViews(views.size());
        }


        statsClient.saveStats(APP, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        return fullDto;
    }

    private void validDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateDateException(" eventDate = (" + eventDate + ") должно содержать дату, которая еще не наступила.");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с  id = " + userId + " не найден"));
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена"));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено"));
    }

    private Event getEventByEventIdAndUserId(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private void updateEvent(Event event, UpdateEventDto updateEventDto) {
        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Category category = getCategoryById(updateEventDto.getCategory());
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            validDate(updateEventDto.getEventDate());
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(ActionEventStatus.CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
            if (updateEventDto.getStateAction().equals(ActionEventStatus.SEND_TO_REVIEW)) {
                event.setState(PENDING);
            }
        }
        if (updateEventDto.getLocation() != null) {
            event.setLat(updateEventDto.getLocation().getLat());
            event.setLon(updateEventDto.getLocation().getLon());
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
            Category category = getCategoryById(eventDto.getCategory());
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
                if (eventDto.getStateAction().equals(ActionEventStatus.REJECT_EVENT)) {
                    event.setState(CANCELED);
                }
                if (eventDto.getStateAction().equals(ActionEventStatus.PUBLISH_EVENT)) {
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
                throw new ValidateDateException(rangeStart + " не может быть позже " + rangeEnd);
            }
        }
    }

    private Long getId(String url) {
        String[] uri = url.split("/");
        return Long.valueOf(uri[uri.length - 1]);
    }

    private void saveViewInEvent(List<ShortEventDto> result) {
        List<String> uris = result
                .stream()
                .map(eventShortDto -> "/events/" + eventShortDto.getId())
                .collect(Collectors.toList());
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

}