package ru.practicum.compilations.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.mapper.EventMapper;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

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


    public static Compilation mapToNewCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilation.setTitle(compilationDto.getTitle());
        return compilation;
    }

}
