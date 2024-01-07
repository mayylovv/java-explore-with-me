package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.dto.HitRequestDto;
import ru.practicum.dto.dto.ViewStats;
import ru.practicum.model.StatSvc;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = { "db.name=test"}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class StatServiceImplTest {

    private final StatService statService;
    private final StatRepository statRepository;
    private HitRequestDto dto1;
    private HitRequestDto dto2;
    private HitRequestDto dto3;

    @BeforeEach
    void setUp() {

        dto1 = new HitRequestDto("ewm-main-service", "/events/1", "192.163.0.1",
                LocalDateTime.of(2023, 9, 6, 11, 0, 0));
        dto2 = new HitRequestDto("ewm-main-service", "/events/2", "192.163.0.1",
                LocalDateTime.of(2023, 9, 6, 11, 0, 23));
        dto3 = new HitRequestDto("ewm-main-service", "/events/2", "192.163.0.1",
                LocalDateTime.of(2023, 9, 6, 11, 1, 23));
    }

    @Test
    void saveStat() {
        statService.saveStat(dto1);
        StatSvc statSvc = statRepository.findById(1).get();

        assertThat(statSvc, notNullValue());
        assertThat(statSvc.getApp(), equalTo(dto1.getApp()));
        assertThat(statSvc.getUri(), equalTo(dto1.getUri()));
        assertThat(statSvc.getIp(), equalTo(dto1.getIp()));
    }

    @Test
    void getStats() {

        LocalDateTime dateTime1 = LocalDateTime.of(2023, 9, 6, 0, 0, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 9, 6, 23, 0, 0);

        statService.saveStat(dto1);
        statService.saveStat(dto2);
        statService.saveStat(dto3);
        List<ViewStats> result = new ArrayList<>(statService.getStats(dateTime1, dateTime2, List.of(), false));

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getHits());
        assertEquals(1, result.get(1).getHits());

        result = new ArrayList<>(statService.getStats(dateTime1, dateTime2, List.of("/events/2"), false));

        assertEquals(1, result.size());
        assertEquals("/events/2", result.get(0).getUri());
        assertEquals(2, result.get(0).getHits());

        result = new ArrayList<>(statService.getStats(dateTime1, dateTime2, List.of(), true));

        assertEquals(2, result.size());
        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(1, result.get(0).getHits());
        assertEquals("/events/2", result.get(1).getUri());
        assertEquals(1, result.get(1).getHits());

        result = new ArrayList<>(statService.getStats(dateTime1, dateTime2, List.of("/events/2"), true));

        assertEquals(1, result.size());
        assertEquals("/events/2", result.get(0).getUri());
        assertEquals(1, result.get(0).getHits());
    }
}