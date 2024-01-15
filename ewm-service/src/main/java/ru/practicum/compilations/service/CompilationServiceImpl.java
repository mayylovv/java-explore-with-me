package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.events.model.Event;
import ru.practicum.util.PaginationSetup;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.handler.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.Messages.*;
import static ru.practicum.compilations.dto.CompilationMapper.mapToNewCompilation;
import static ru.practicum.compilations.dto.CompilationMapper.mapToCompilationDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    private Compilation getCompilation(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
    }

    @Override
    public List<CompilationDto> getAllCompilation(Boolean pinned, Integer from, Integer size) {
        log.info(GET_MODELS.getMessage());
        if (pinned == null) {
           return compilationRepository.findAll(new PaginationSetup(from, size, Sort.unsorted())).getContent().stream()
                    .map(CompilationMapper::mapToCompilationDto)
                    .collect(Collectors.toList());
        }

        return compilationRepository.findAllByPinned(pinned, new PaginationSetup(from, size, Sort.unsorted()))
                .getContent().stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = getCompilation(id);
        log.info(GET_MODEL_BY_ID.getMessage(), id);
        return mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto saveCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = mapToNewCompilation(compilationDto);

        if (compilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
            compilation.setEvents(events);
        }
        log.info(SAVE_MODEL.getMessage(), compilation);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        log.info(DELETE_MODEL.getMessage(), compId);
        getCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationByID(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = getCompilation(compId);
        Boolean pinned = compilationDto.getPinned();
        String title = compilationDto.getTitle();

        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        }
        if (pinned != null) {
            compilation.setPinned(pinned);
        }
        if (title != null) {
            compilation.setTitle(title);
        }
        log.info(UPDATE_MODEL.getMessage(), compilation);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }
}