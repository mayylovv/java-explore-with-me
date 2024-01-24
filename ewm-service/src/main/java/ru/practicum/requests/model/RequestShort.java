package ru.practicum.requests.model;

import lombok.Data;

@Data
public class RequestShort {
    private Long id;
    private Long countRequest;

    public RequestShort(Long id, Long countRequest) {
        this.id = id;
        this.countRequest = countRequest;
    }
}
