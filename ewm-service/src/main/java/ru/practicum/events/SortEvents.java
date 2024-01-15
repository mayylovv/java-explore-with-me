package ru.practicum.events;

import java.util.Optional;

public enum SortEvents {
    // Сортировка по дате
    EVENT_DATE,
    // Сортировка по количеству просмотров
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
