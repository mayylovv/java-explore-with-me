package ru.practicum.requests.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.requests.enums.RequestStatus;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestStatusParticipation {

    Set<Long> requestIds;
    RequestStatus status;
}