package ru.practicum.events.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.category.model.Category;
import ru.practicum.events.enums.EventStatus;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;
import static ru.practicum.events.enums.EventStatus.PENDING;

@Data
@Entity
@DynamicUpdate
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation", nullable = false, length = 2000)
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "description", nullable = false, length = 7000)
    String description;

    @DateTimeFormat(pattern = PATTERN_DATE)
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @Column(name = "location_lat", nullable = false)
    float lat;

    @Column(name = "location_lon", nullable = false)
    float lon;

    @Column(name = "paid", nullable = false)
    Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    Integer participantLimit;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests = 0;

    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration;

    @Column(name = "title", nullable = false, length = 120)
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    EventStatus state = PENDING;

    @DateTimeFormat(pattern = PATTERN_DATE)
    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn = LocalDateTime.now();

    @DateTimeFormat(pattern = PATTERN_DATE)
    @Column(name = "published_on")
    LocalDateTime publishedOn;
}