package ru.practicum.events.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.categories.model.Category;
import ru.practicum.events.EventState;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;
import static ru.practicum.events.EventState.PENDING;

@Entity
@Table(name = "events")
@ToString
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamicUpdate
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;  // Идентификатор;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation; // Краткое описание;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // категория к которой относится событие;

    @Column(name = "description", nullable = false, length = 7000)
    private String description; // Полное описание события;

    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = PATTERN_DATE)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие;

    @Column(name = "location_lat", nullable = false)
    private float lat; // Широта;

    @Column(name = "location_lon", nullable = false)
    private float lon; // Долгота;

    @Column(name = "paid", nullable = false)
    private Boolean paid; // Нужно ли оплачивать участие в событии. Default: false;

    @Column(name = "participant_limit", nullable = false)
    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения. Default: 0;
    private Integer participantLimit;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0; // Количество одобренных заявок на участие в данном событии;

    /* Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут ожидать подтверждения инициатором события.
       Если false - то будут подтверждаться автоматически. Default: true*/
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "title", nullable = false, length = 120)
    private String title; // Заголовок события;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state = PENDING; // Состояние жизненного цикла события

    @Column(name = "created_on", nullable = false)
    @DateTimeFormat(pattern = PATTERN_DATE)
    private LocalDateTime createdOn = LocalDateTime.now(); // Дата и время создания события

    @Column(name = "published_on")
    @DateTimeFormat(pattern = PATTERN_DATE)
    private LocalDateTime publishedOn; // Дата и время публикации события
}