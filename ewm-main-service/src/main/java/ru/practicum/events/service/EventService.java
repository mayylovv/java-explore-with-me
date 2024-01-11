package ru.practicum.events.service;

import ru.practicum.events.enums.EventStatus;
import ru.practicum.events.enums.EventSort;
import ru.practicum.events.dto.FullEventDto;
import ru.practicum.events.dto.ShortEventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    FullEventDto createEventByUserId(Long userId, NewEventDto eventDto);


    List<ShortEventDto> getAllEventsByUserId(Long userId, int from, int size);

    FullEventDto getEventByUserId(Long userId, Long eventId);

    FullEventDto updateEventById(Long userId, Long eventId, UpdateEventDto eventDto);


    List<FullEventDto> getAllEventsForAdmin(List<Long> users, List<EventStatus> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    FullEventDto updateEventByIdForAdmin(Long eventId, UpdateEventDto eventDto);

    List<ShortEventDto> getEventsForPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                                           Integer size, HttpServletRequest request);

    FullEventDto getEventByIdForPublic(Long id, HttpServletRequest request);
}