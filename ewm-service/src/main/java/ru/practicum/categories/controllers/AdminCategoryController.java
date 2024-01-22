package ru.practicum.categories.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto saveCategory(@Valid @RequestBody CategoryDto newCategory) {
        log.info("POST '/admin/categories'. Запрос на добавление новой категории {} ", newCategory);
        CategoryDto response = categoryService.saveCategory(newCategory);
        log.info("POST '/admin/categories'. Ответ, новая категория успешно добавлена {} ", response);
        return response;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "catId") Long catId) {
        log.info("DELETE '/admin/categories/{catId}'. Запрос на удаление категории с catId {} ", catId);
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable(value = "catId") Long catId, @Valid @RequestBody CategoryDto dto) {
        log.info("PATCH '/admin/categories/{catId}'. Запрос, обновление категории c id {} и телом {} ", catId, dto);
        CategoryDto response = categoryService.updateCategoryById(catId, dto);
        log.info("PATCH '/admin/categories/{catId}'. Ответ, категория c id {} обновлена {} ", catId, response);
        return response;
    }
}
