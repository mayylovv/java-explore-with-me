package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.dto.HitRequestDto;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.dto.util.Constant.DATE_PATTERN;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Информация сохранена")
    public void saveEndpointHit(@Valid @RequestBody HitRequestDto hitRequestDto) {
        log.info("Сохранить HitRequestDto {}", hitRequestDto);
        service.saveStat(hitRequestDto);
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getViewStats(@RequestParam(value = "start") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
                                              @RequestParam(value = "end") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
                                              @RequestParam(defaultValue = "") List<String> uris,
                                              @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение статистики с параметрами: дата начала {}, дата окончания {}, список URL-адресов {}, " +
                "уникальные сущности {},", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}