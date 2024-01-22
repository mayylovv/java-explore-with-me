package ru.practicum.compilations.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {

    private final CompilationService serviceCompilation;

    @GetMapping
    public Collection<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET '/compilations'. Запрос на получение всех подборок событий");
        Collection<CompilationDto> response = serviceCompilation.getAllCompilations(pinned, from, size);
        log.info("GET '/compilations'. Ответ, все события {}", response);
        return response;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("GET '/compilations/{compId}'. Запрос на получение подборки с id {} ", compId);
        CompilationDto response = serviceCompilation.getCompilation(compId);
        log.info("GET '/compilations/{compId}'. Ответ, подборка с id {} и телом {} ", compId, response);
        return response;
    }
}
