package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.dto.HitRequestDto;
import ru.practicum.model.StatSvc;

@UtilityClass
public class StatMapper {

    public static StatSvc mapToStat(HitRequestDto hitRequestDto) {
        StatSvc statSvc = new StatSvc();
        statSvc.setApp(hitRequestDto.getApp());
        statSvc.setUri(hitRequestDto.getUri());
        statSvc.setIp(hitRequestDto.getIp());
        statSvc.setTimestamp(hitRequestDto.getTimestamp());
        return statSvc;
    }
}