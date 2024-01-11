package ru.practicum.requests.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestStatusСonfirmation {

    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}