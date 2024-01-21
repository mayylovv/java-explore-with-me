package ru.practicum.users.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_unique", columnNames = "email")
        }
)
@ToString
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id; // Идентификатор пользователя;

    @Column(
            name = "email",
            nullable = false,
            length = 254
    )
    @EqualsAndHashCode.Include
    private String email; // Почтовый адрес.

    @Column(
            name = "name",
            nullable = false,
            length = 250
    )
    private String name; // Имя пользователя;
}