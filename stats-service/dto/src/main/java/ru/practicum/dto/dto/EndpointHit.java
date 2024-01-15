package ru.practicum.dto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.dto.Constant.DATE_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {

    @NotBlank
    private String app; // Идентификатор сервиса для которого записывается информация, example: ewm-main-service;
    @NotBlank
    private String uri; // URI для которого был осуществлен запрос, example: /events/1;
    private String ip; // IP-адрес пользователя, осуществившего запрос, example: 192.163.0.1;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime timestamp; // Дата и время, когда был совершен запрос к эндпоинту
}