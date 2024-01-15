package ru.practicum.requests.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.requests.EventRequestStatus;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_CREATED_DATE;
import static ru.practicum.requests.EventRequestStatus.PENDING;

@Entity
@Table(name = "requests")
@ToString
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamicUpdate
public class ParticipationRequest { //Заявка на участие в событии

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false,
            unique = true
    )
    private Long id; // Идентификатор заявки

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "event_id")
    private Event event; // Событие;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requester_id", updatable = false)
    private User requester; // Пользователь, отправивший заявку;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventRequestStatus status = PENDING; // Статус заявки.

    @Column(name = "created_date", nullable = false, updatable = false)
    @DateTimeFormat(pattern = PATTERN_CREATED_DATE)
    private LocalDateTime created = LocalDateTime.now(); // Дата и время создания заявки;
}