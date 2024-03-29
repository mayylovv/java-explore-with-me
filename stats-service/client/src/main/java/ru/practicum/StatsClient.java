package ru.practicum;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.dto.EndpointHit;
import ru.practicum.dto.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@PropertySource(value = {"classpath:statsClient.properties"})
public class StatsClient {

    private static final String value = "${stats.server.url}";
    private final WebClient client;

    public StatsClient(@Value(value) String baseUrl) {
        this.client = WebClient.create(baseUrl);
    }

    public void saveStats(String app, String uri, String ip, LocalDateTime timestamp) {
        final EndpointHit endpointHit = new EndpointHit(app, uri, ip, timestamp);

        this.client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new EndpointHit(app, uri, ip, timestamp))
                .retrieve()
                .toBodilessEntity()
                .doOnNext(c -> log.info("Сохранить статистику {}", endpointHit))
                .block();
    }

    public ResponseEntity<List<ViewStats>> getStats(String start, String end, List<String> uris, Boolean unique) {
        return this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(ViewStats.class)
                .doOnNext(c -> log.info("Получение статистики с параметрами: дата начала {}, дата окончания {}, URI {}, " +
                        "уникальные {}", start, end, uris, unique))
                .block();
    }
}