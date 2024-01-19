package ru.practicum.events.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Location {

    @NotNull
    Float lat;

    @NotNull
    Float lon;
}