package ru.practicum.requests.dto;

import lombok.Data;
import ru.practicum.requests.EventRequestStatus;

import java.util.Set;

@Data
public class RequestStatusParticipation {
    private Set<Long> requestIds;
    private EventRequestStatus status;
}
