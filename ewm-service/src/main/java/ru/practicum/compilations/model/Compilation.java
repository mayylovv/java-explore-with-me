package ru.practicum.compilations.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.events.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@ToString
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;  // Идентификатор;

    @Column(name = "pinned", nullable = false)
    private Boolean pinned; // Закреплена ли подборка на главной странице сайта;

    @Column(name = "title", nullable = false)
    private String title; // Заголовок подборки.

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;
}