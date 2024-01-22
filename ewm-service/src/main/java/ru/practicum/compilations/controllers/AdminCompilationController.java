package ru.practicum.compilations.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("GET '/admin/compilations'. Запрос на добавление новой подборки {} ", compilationDto);
        CompilationDto response = compilationService.saveCompilation(compilationDto);
        log.info("GET '/admin/compilations'. Ответ, новая подборка успешно добавлена {} ", response);
        return response;
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable(value = "compId") Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        log.info("PATCH '/admin/compilations/{compId}'. Запрос на обновление подборки с id {} ", compId);
        CompilationDto response = compilationService.updateCompilation(compId, compilationDto);
        log.info("PATCH '/admin/compilations/{compId}'. Ответ, подборка с id {} успешно обновлена {} ", compId, response);
        return response;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "compId") Long compId) {
        log.info("DELETE '/admin/compilations/{compId}'. Запрос на удаление подборки с id {} ", compId);
        compilationService.deleteCompilation(compId);
    }
}
