package ru.practicum.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.model.StatSvc;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatSvc, Integer> {

    @Query(
            "select new ru.practicum.dto.dto.ViewStats(st.app, st.uri, count(distinct st.ip) as hits) " +
            "from StatSvc as st " +
            "where st.timestamp between :start and :end " +
            "group by st.app, st.uri "
    )
    List<ViewStats> getStatsWithUniqueIp(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(
            "select new ru.practicum.dto.dto.ViewStats(st.app, st.uri, count(st.ip) as hits) " +
            "from StatSvc as st " +
            "where st.timestamp between :start and :end " +
            "group by st.app, st.uri "
    )
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(
            "select new ru.practicum.dto.dto.ViewStats(st.app, st.uri, count(distinct st.ip) as hits) " +
            "from StatSvc as st " +
            "where st.timestamp between :start and :end " +
            "and st.uri in :uris " +
            "group by st.app, st.uri"
    )
    List<ViewStats> getStatsByUrisListWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris, Sort sort);

    @Query(
            "select new ru.practicum.dto.dto.ViewStats(st.app, st.uri, count(st.ip) as hits) " +
            "from StatSvc as st " +
            "where st.timestamp between :start and :end " +
            "and st.uri in :uris " +
            "group by st.app, st.uri "
    )
    List<ViewStats> getStatsByUrisList(LocalDateTime start, LocalDateTime end, List<String> uris, Sort sort);
}