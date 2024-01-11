package ru.practicum.events.enums;

import java.util.Optional;

public enum EventSort {

    EVENT_DATE,

    VIEWS;

    public static Optional<EventSort> from(String stringState) {
        for (EventSort state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
