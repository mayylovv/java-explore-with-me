package ru.practicum.events.enums;

import java.util.Optional;

public enum SortEvents {

    EVENT_DATE,
    VIEWS;

    public static Optional<SortEvents> from(String stringState) {
        for (SortEvents state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
