package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(CategoryDto categoryDto);

    void deleteCategoryById(Long id);

    CategoryDto updateCategoryById(Long id, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId);
}
