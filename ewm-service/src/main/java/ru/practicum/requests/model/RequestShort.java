package ru.practicum.requests.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestShort {
    private Long id;
    private Long countRequest;

    public RequestShort(Long id, Long countRequest) {
        this.id = id;
        this.countRequest = countRequest;
    }
}
