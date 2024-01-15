package ru.practicum.categories.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
        @UniqueConstraint(name = "category_name_unique", columnNames = "name")}
)
@Getter
@Setter
@ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;  // Идентификатор;

    @Column(
            name = "name",
            nullable = false,
            length = 50
    )
    private String name;
}