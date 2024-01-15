package ru.practicum.requests;

public enum EventRequestStatus {
    // Статусы заявки на участие в событии
    CONFIRMED, // Подтвержденный
    REJECTED, // Отклоненный
    PENDING, // Ожидает рассмотрения
    CANCELED // отмена (пользователем)
}