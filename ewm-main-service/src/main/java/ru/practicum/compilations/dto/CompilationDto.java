package ru.practicum.compilations.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.dto.ShortEventDto;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    List<ShortEventDto> events;
    long id;
    boolean pinned;
    String title;
}