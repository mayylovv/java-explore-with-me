package ru.practicum.events.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;
import ru.practicum.events.dto.Additionalnformation;
import ru.practicum.events.dto.FullEventDto;
import ru.practicum.events.dto.ShortEventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.users.model.User;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.users.mapper.UserMapper.toUserShortDto;

@UtilityClass
public class EventMapper {

    public static Event mapToNewEvent(NewEventDto eventDto, User user, Category category) {

        Event newEvent = new Event();
        newEvent.setAnnotation(eventDto.getAnnotation());
        newEvent.setCategory(category);
        newEvent.setDescription(eventDto.getDescription());
        newEvent.setEventDate(eventDto.getEventDate());
        newEvent.setLat(eventDto.getLocation().getLat());
        newEvent.setLon(eventDto.getLocation().getLon());
        newEvent.setPaid(eventDto.getPaid());
        newEvent.setParticipantLimit(eventDto.getParticipantLimit());
        newEvent.setRequestModeration(eventDto.getRequestModeration());
        newEvent.setTitle(eventDto.getTitle());
        newEvent.setInitiator(user);

        return newEvent;
    }

    public static FullEventDto mapToFullEventDto(Event event) {

        FullEventDto fullEventDto = new FullEventDto();
        fullEventDto.setAnnotation(event.getAnnotation());
        fullEventDto.setConfirmedRequests(event.getConfirmedRequests());
        fullEventDto.setEventDate(event.getEventDate());
        fullEventDto.setId(event.getId());
        Location location = new Location(event.getLat(), event.getLon());
        fullEventDto.setLocation(location);
        fullEventDto.setPaid(event.getPaid());
        fullEventDto.setParticipantLimit(event.getParticipantLimit());
        fullEventDto.setTitle(event.getTitle());
        fullEventDto.setState(event.getState());
        Additionalnformation eventInformation = new Additionalnformation();
        eventInformation.setDescription(event.getDescription());
        eventInformation.setCategory(toCategoryDto(event.getCategory()));
        eventInformation.setCreatedOn(event.getCreatedOn());
        eventInformation.setInitiator(toUserShortDto(event.getInitiator()));
        eventInformation.setRequestModeration(event.getRequestModeration());

        if (event.getPublishedOn() != null) {
            eventInformation.setPublishedOn(event.getPublishedOn());
        }
        fullEventDto.setEventInformation(eventInformation);

        return fullEventDto;
    }

    public static ShortEventDto mapToShortEventDto(Event event) {

        ShortEventDto shortDto = new ShortEventDto();
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(toCategoryDto(event.getCategory()));
        shortDto.setConfirmedRequests(event.getConfirmedRequests());
        shortDto.setEventDate(event.getEventDate());
        shortDto.setId(event.getId());
        shortDto.setInitiator(toUserShortDto(event.getInitiator()));
        shortDto.setPaid(event.getPaid());
        shortDto.setTitle(event.getTitle());

        return shortDto;
    }
}