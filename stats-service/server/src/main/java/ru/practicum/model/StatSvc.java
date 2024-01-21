package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatSvc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private int id; // Идентификатор записи;

    @Column(name = "app", nullable = false)
    private String app; // Идентификатор сервиса для которого записывается информация, example: ewm-main-StatService.StatService;

    @Column(name = "uri", nullable = false)
    private String uri; // URI для которого был осуществлен запрос, example: /events/1;

    @Column(name = "ip")
    private String ip; // IP-адрес пользователя, осуществившего запрос, example: 192.163.0.1;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp; // Дата и время, когда был совершен запрос к эндпоинту
    // (в формате "yyyy-MM-dd HH:mm:ss"), example: 2022-09-06 11:00:23.
}