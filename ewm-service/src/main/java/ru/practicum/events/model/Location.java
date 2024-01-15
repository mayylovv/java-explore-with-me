package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class Location {

    @NotNull
    private Float lat; // Широта;

    @NotNull
    private Float lon; // Долгота;
}