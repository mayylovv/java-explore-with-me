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

    private final StatRepository statrepository;

    @Override
    @Transactional
    public void saveStat(HitRequestDto dto) {
        StatSvc stat = statrepository.save(StatMapper.mapToStat(dto));
        log.info("Сохранить статистику {}", stat);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Sort sort = Sort.by(Sort.Direction.DESC, "hits");
        if (start.isAfter(end)) {
            throw new ValidateDateException("Дата окончания должна быть больше или равна дате начала. " +
                    "Введённые даты: начало - " + start + ", окончание - " + end);
        }
        if (uris.isEmpty()) {
            if (unique) {
                return statrepository.getStatsWithUniqueIp(start, end, sort);
            }
            return statrepository.getStats(start, end, sort);
        }
        if (unique) {
            return statrepository.getStatsByUrisListWithUniqueIp(start, end, uris, sort);
        }
        return statrepository.getStatsByUrisList(start, end, uris, sort);
    }
}