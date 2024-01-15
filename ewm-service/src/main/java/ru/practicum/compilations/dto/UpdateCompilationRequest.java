package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events; // Список идентификаторов событий входящих в подборку;
    private Boolean pinned = false; // Закреплена ли подборка на главной странице сайта;

    @Size(min = 1, max = 50)
    private String title; // Заголовок подборки.
}
