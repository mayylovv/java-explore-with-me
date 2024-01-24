package ru.practicum.events.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.categories.model.Category;
import ru.practicum.events.enums.EventState;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_DATE;
import static ru.practicum.events.enums.EventState.PENDING;

@Data
@Entity
@DynamicUpdate
@Table(name = "events")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    Long id;

    @Column(name = "annotation", nullable = false, length = 2000)
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "description", nullable = false, length = 7000)
    String description;

    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = PATTERN_DATE)
    LocalDateTime eventDate;

    @Column(name = "location_lat", nullable = false)
    float lat;

    @Column(name = "location_lon", nullable = false)
    float lon;

    @Column(name = "paid", nullable = false)
    Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    Integer participantLimit;

    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration;

    @Column(name = "title", nullable = false, length = 120)
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    EventState state = PENDING;

    @Column(name = "created_on", nullable = false)
    @DateTimeFormat(pattern = PATTERN_DATE)
    LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    @DateTimeFormat(pattern = PATTERN_DATE)
    LocalDateTime publishedOn;
}
