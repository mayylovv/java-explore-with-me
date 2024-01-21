package ru.practicum.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Messages {

    SAVE_MODEL("Saving {}"),
    DELETE_MODEL("Delete by id {}"),
    UPDATE_MODEL("Update {}"),
    GET_MODEL_BY_ID("Get by id {}"),
    GET_MODELS("Get models"),
    SAVE_STATS("Save stats"),
    GET_STATS("Get view"),
    UPDATE_STATUS("Update status");


    private final String message;
}