package ru.practicum.categories.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(name = "category_name_unique", columnNames = "name")})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    Long id;

    @Column(name = "name", nullable = false, length = 50)
    String name;

}