package ru.practicum.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id; // Идентификатор категории;

    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(min = 1, max = 50)
    private String name; // Название категории. maxLength: 50 minLength: 1
}