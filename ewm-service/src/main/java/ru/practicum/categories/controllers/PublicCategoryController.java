package ru.practicum.categories.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Collection<CategoryDto> getCategory(@RequestParam(value = "from", defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET '/categories'. Запрос на получение {} категорий и размером {}", from, size);
        Collection<CategoryDto> response = categoryService.getCategories(from, size);
        log.info("GET '/categories'. Ответ, {}", response);
        return response;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("GET '/categories/{catId}'. Запрос на получение категории с id {}", catId);
        CategoryDto response = categoryService.getCategory(catId);
        log.info("GET '/categories/{catId}'. Ответ, категория с id {}", response);
        return response;
    }
}
