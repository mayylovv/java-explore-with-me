package ru.practicum.requests.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.events.model.Event;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.users.model.User;

import static ru.practicum.requests.EventRequestStatus.CONFIRMED;

@UtilityClass
public class RequestMapper {

    public static Request mapToNewParticipationRequest(Event event, User user) {
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(CONFIRMED);
        }
        return request;
    }

    public static RequestDto mapToParticipationRequestDto(Request request) {
        RequestDto requestDto = new RequestDto(
                request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
        return requestDto;
    }
}
