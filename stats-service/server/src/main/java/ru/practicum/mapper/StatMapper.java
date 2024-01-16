package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.dto.EndpointHit;
import ru.practicum.model.StatSvc;

@UtilityClass
public class StatMapper {

    public StatSvc mapToStat(EndpointHit endpointHit) {
        StatSvc statSvc = new StatSvc();
        statSvc.setApp(endpointHit.getApp());
        statSvc.setUri(endpointHit.getUri());
        statSvc.setIp(endpointHit.getIp());
        statSvc.setTimestamp(endpointHit.getTimestamp());
        return statSvc;
    }
}