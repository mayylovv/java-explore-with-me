package ru.practicum.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {

    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_CREATED_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String APP = "ewm-main-service"; // Название сервиса, example: ewm-main-service;
    public static final String START_DATE = LocalDateTime.now().minusDays(1000)
            .format(DateTimeFormatter.ofPattern(PATTERN_DATE));
    public static final String END_DATE = LocalDateTime.now().plusDays(1000)
            .format(DateTimeFormatter.ofPattern(PATTERN_DATE));
}