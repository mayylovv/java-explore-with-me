package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilation(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long id);

    CompilationDto saveCompilation(NewCompilationDto compilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilationByID(Long compId, UpdateCompilationRequest compilationDto);
}