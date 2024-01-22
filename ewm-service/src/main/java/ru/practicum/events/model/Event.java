package ru.practicum.events.model;

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
@DynamicUpdate
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "description", nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = PATTERN_DATE)
    private LocalDateTime eventDate;
    @Column(name = "location_lat", nullable = false)
    private float lat;
    @Column(name = "location_lon", nullable = false)
    private float lon;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Column(name = "title", nullable = false, length = 120)
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state = PENDING;
    @Column(name = "created_on", nullable = false)
    @DateTimeFormat(pattern = PATTERN_DATE)
    private LocalDateTime createdOn = LocalDateTime.now();
    @Column(name = "published_on")
    @DateTimeFormat(pattern = PATTERN_DATE)
    private LocalDateTime publishedOn;
}
