package ru.practicum.comments.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@ToString
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamicUpdate
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id; // Идентификатор пользователя;

    @Column (nullable = false)
    private String text; // содержимое комментария;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "event_id")
    private Event event; // вещь, к которой относится комментарий;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "author_id", updatable = false)
    private User author; // автор комментария;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now(); // дата создания комментария.
}