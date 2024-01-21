package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.dto.EndpointHit;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.exception.ValidateDateException;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.dto.Constant.DATE_PATTERN;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {

    private final StatService service;

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidateDateException("The end date cannot be before than the start date");
        }
    }

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Информация сохранена")
    public void saveEndpointHit(@Valid @RequestBody EndpointHit dto) {
        log.info("Save EndpointHit {}", dto);
        service.saveStat(dto);
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getViewStats(
            // Дата и время начала диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
            @RequestParam("start") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,

            // Дата и время конца диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
            @RequestParam("end") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,

            // Список uri для которых нужно выгрузить статистику, необязательный параметр.
            @RequestParam(defaultValue = "") List<String> uris,

            // Нужно ли учитывать только уникальные посещения (только с уникальным ip), Default value : false
            @RequestParam(defaultValue = "false") Boolean unique
            ) {

        validateDate(start, end);
        log.info("Get stats with parameters start date {} end date {} urls list {} unique {}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}