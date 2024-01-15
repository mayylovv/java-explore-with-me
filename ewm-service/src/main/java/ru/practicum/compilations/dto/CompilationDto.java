package ru.practicum.compilations.dto;

import lombok.Data;
import ru.practicum.events.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {

    private List<EventShortDto> events; // Список событий входящих в подборку;
    private long id; // Идентификатор;
    private boolean pinned; // Закреплена ли подборка на главной странице сайта;
    private String title; // Заголовок подборки.
}