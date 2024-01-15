package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.handler.NotFoundException;
import ru.practicum.util.PaginationSetup;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilations.mapper.CompilationMapper.mapToCompilationDto;
import static ru.practicum.compilations.mapper.CompilationMapper.mapToNewCompilation;

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
        log.info("Получение");
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
        log.info("Получение по id = {}", id);
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
        log.info("Сохранение {}", compilation);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        log.info("Удаление по id = {}", compId);
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
        log.info("Обновление {}", compilation);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }
}