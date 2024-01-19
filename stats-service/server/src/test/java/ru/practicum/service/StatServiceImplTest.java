package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.dto.EndpointHit;
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
@SpringBootTest(
        properties = { "db.name=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatServiceImplTest {

    private final StatService service;
    private final StatRepository repository;
    private EndpointHit dto;
    private EndpointHit dto2;
    private EndpointHit dto3;

    @BeforeEach
    void setUp() {
        dto = new EndpointHit("ewm-main-service", "/events/1", "192.163.0.1",
                LocalDateTime.of(2022, 9, 6, 11, 0, 0));
        dto2 = new EndpointHit("ewm-main-service", "/events/2", "192.163.0.1",
                LocalDateTime.of(2022, 9, 6, 11, 0, 23));
        dto3 = new EndpointHit("ewm-main-service", "/events/2", "192.163.0.1",
                LocalDateTime.of(2022, 9, 6, 11, 1, 23));
    }

    @Test
    void saveStat() {
        service.saveStat(dto);
        StatSvc result = repository.findById(1).get();

        assertThat(result, notNullValue());
        assertThat(result.getApp(), equalTo(dto.getApp()));
        assertThat(result.getUri(), equalTo(dto.getUri()));
        assertThat(result.getIp(), equalTo(dto.getIp()));
    }

    @Test
    void getStats() {
        service.saveStat(dto);
        service.saveStat(dto2);
        service.saveStat(dto3);
        List<ViewStats> result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2022, 9, 6, 0, 0, 0),
                LocalDateTime.of(2022, 9, 6, 23, 0, 0),
                List.of(), false));

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getHits());
        assertEquals(1, result.get(1).getHits());

        result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2022, 9, 6, 0, 0, 0),
                LocalDateTime.of(2022, 9, 6, 23, 0, 0),
                List.of("/events/2"), false));

        assertEquals(1, result.size());
        assertEquals("/events/2", result.get(0).getUri());
        assertEquals(2, result.get(0).getHits());

        result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2022, 9, 6, 0, 0, 0),
                LocalDateTime.of(2022, 9, 6, 23, 0, 0),
                List.of(), true));

        assertEquals(2, result.size());
        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(1, result.get(0).getHits());
        assertEquals("/events/2", result.get(1).getUri());
        assertEquals(1, result.get(1).getHits());

        result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2022, 9, 6, 0, 0, 0),
                LocalDateTime.of(2022, 9, 6, 23, 0, 0),
                List.of("/events/2"), true));

        assertEquals(1, result.size());
        assertEquals("/events/2", result.get(0).getUri());
        assertEquals(1, result.get(0).getHits());
    }
}