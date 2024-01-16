package ru.practicum.service;

import ru.practicum.dto.dto.EndpointHit;
import ru.practicum.dto.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatService {

    void saveStat(EndpointHit dto);

    Collection<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}