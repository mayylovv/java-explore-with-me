package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.model.Location;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;
    String title;
    String annotation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
    LocalDateTime eventDate;
    Location location;
    boolean paid;
    int participantLimit;
    long confirmedRequests;
    long views;
    EventState state;

    @JsonUnwrapped
    public AdditionalEventInformation eventInformation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AdditionalEventInformation {

        String description;
        CategoryDto category;
        boolean requestModeration;
        UserShortDto initiator;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
        LocalDateTime publishedOn;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE)
        LocalDateTime createdOn;
    }
}