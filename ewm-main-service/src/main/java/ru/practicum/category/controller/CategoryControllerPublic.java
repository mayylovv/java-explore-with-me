package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    public Collection<CategoryDto> getCategory(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получение категории с параметрами {} размерами {}", from, size);
        return categoryService.getAllCategory(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Получение категории с id {}", catId);
        return categoryService.getCategoryById(catId);
    }
}