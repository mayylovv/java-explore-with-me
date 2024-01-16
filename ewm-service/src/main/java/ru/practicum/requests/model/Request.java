package ru.practicum.requests.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.requests.EventRequestStatus;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_CREATED_DATE;
import static ru.practicum.requests.EventRequestStatus.PENDING;

@Data
@Entity
@DynamicUpdate
@Table(name = "requests")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requester_id", updatable = false)
    User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    EventRequestStatus status = PENDING;

    @Column(name = "created_date", nullable = false, updatable = false)
    @DateTimeFormat(pattern = PATTERN_CREATED_DATE)
    LocalDateTime created = LocalDateTime.now();

}