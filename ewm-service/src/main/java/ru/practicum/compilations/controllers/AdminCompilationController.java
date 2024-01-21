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
        log.info("Creating compilation {}", compilationDto);
        return compilationService.saveCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable(value = "compId") Long compId,
                                        @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        log.info("Update compilation {} by Id {}", compilationDto, compId);
        return compilationService.updateCompilationByID(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "compId") Long compId) {
        log.info("Deleting compilation by Id {}", compId);
        compilationService.deleteCompilationById(compId);
    }
}