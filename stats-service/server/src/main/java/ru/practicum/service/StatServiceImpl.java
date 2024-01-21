package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.dto.EndpointHit;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.model.StatMapper;
import ru.practicum.model.StatSvc;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final StatRepository repository;

    @Override
    @Transactional
    public void saveStat(EndpointHit dto) {
        StatSvc stat = repository.save(StatMapper.mapToStat(dto));
        log.info("Save stat {}", stat);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Sort sort = Sort.by(Sort.Direction.DESC, "hits");

        if (uris.isEmpty()) {
            if (unique) {
                return repository.getStatsWithUniqueIp(start, end, sort);
            }

            return repository.getStats(start, end, sort);
        }
        if (unique) {
            return repository.getStatsByUrisListWithUniqueIp(start, end, uris, sort);
        }

        return repository.getStatsByUrisList(start, end, uris, sort);
    }
}