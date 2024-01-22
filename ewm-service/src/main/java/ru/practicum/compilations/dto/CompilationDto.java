package ru.practicum.compilations.dto;

import lombok.Data;
import ru.practicum.events.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private List<EventShortDto> events;
    private long id;
    private boolean pinned;
    private String title;
}
