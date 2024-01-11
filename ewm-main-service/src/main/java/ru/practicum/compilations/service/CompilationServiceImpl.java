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
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilations.mapper.CompilationMapper.mapToNewCompilation;
import static ru.practicum.compilations.mapper.CompilationMapper.mapToCompilationDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapToNewCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }
        log.info("Сохранение {}", compilation);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
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
        Compilation compilation = findCompilationById(id);
        log.info("Получение по id = {}", id);
        return mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long id) {
        log.info("Удаление по id = {}", id);
        findCompilationById(id);
        compilationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationByID(Long id, UpdateCompilationRequest compilationDto) {
        Compilation compilation = findCompilationById(id);
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

        private Compilation findCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Компиляция с id = " + id + " не найдена"));
    }
}