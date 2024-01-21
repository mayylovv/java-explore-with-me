package ru.practicum.compilations.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventMapper;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static Compilation mapToNewCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilation.setTitle(compilationDto.getTitle());

        return compilation;
    }

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        if (compilation.getEvents() != null)
            dto.setEvents(compilation.getEvents()
                    .stream()
                    .map(EventMapper::mapToEventShortDto)
                    .collect(Collectors.toList()));
        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());
        return dto;
    }
}