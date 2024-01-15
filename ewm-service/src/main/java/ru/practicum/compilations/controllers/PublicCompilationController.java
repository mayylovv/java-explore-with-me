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
    public Collection<CompilationDto> getCompilation(
            // искать только закрепленные/не закрепленные подборки, возможно null
            @RequestParam(required = false) Boolean pinned,

            // количество элементов, которые нужно пропустить для формирования текущего набора
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,

            // количество элементов в наборе
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get compilations with parameters pinned {} from {} size {}", pinned, from, size);
        return serviceCompilation.getAllCompilation(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Get compilation with id {}", compId);
        return serviceCompilation.getCompilationById(compId);
    }
}