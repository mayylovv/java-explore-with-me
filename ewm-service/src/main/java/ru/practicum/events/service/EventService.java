package ru.practicum.events.service;

import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.SortEvents;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    List<EventShortDto> getAllEventsByUserId(Long userId, int from, int size);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEventById(Long userId, Long eventId, UpdateEventDto eventDto);

    List<EventFullDto> getAllEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByIdAdmin(Long eventId, UpdateEventDto eventDto);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, SortEvents sort, Integer from,
                                        Integer size, String url, String ip);

    EventFullDto getEventByIdPublic(Long id, String url, String ip);
}
