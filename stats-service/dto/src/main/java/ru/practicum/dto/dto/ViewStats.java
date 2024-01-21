package ru.practicum.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {

    private String app; // Название сервиса, example: ewm-main-service;
    private String uri; // URI сервиса, example: /events/1;
    private long hits; // Количество просмотров, example: 6.
}