package ru.practicum.categories.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryControllerAdmin {

    private final CategoryService categoryService;
    static final String CATEGORY_PATH = "/{catId}";

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto saveCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Создание категории {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping(CATEGORY_PATH)
    public CategoryDto updateCategory(@PathVariable(value = "catId") Long catId,
                                      @Valid @RequestBody CategoryDto dto) {
        log.info("Обновление категории {} с id {}", dto, catId);
        return categoryService.updateCategory(catId, dto);
    }

    @DeleteMapping(CATEGORY_PATH)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "catId") Long catId) {
        log.info("Удаление категории с id {}", catId);
        categoryService.deleteCategory(catId);
    }
}
