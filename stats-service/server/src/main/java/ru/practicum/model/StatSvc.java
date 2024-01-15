package ru.practicum.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stat")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatSvc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    int id;

    @Column(name = "app", nullable = false)
    String app;

    @Column(name = "uri", nullable = false)
    String uri;

    @Column(name = "ip")
    String ip;

    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp;
}