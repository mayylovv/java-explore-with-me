package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.dto.HitRequestDto;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.exception.ValidateDateException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.StatSvc;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final StatRepository repository;

    @Override
    @Transactional
    public void saveStat(HitRequestDto dto) {
        StatSvc stat = repository.save(StatMapper.mapToStat(dto));
        log.info("Сохранить статистику {}", stat);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Sort sort = Sort.by(Sort.Direction.DESC, "hits");
        if (start.isAfter(end)) {
            throw new ValidateDateException("Дата окончания должна быть больше или равна дате начала");
        }
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