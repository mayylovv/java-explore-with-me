package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.util.PaginationSetup;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.handler.ValidateException;
import ru.practicum.handler.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.Messages.*;
import static ru.practicum.categories.dto.CategoryMapper.toCategory;
import static ru.practicum.categories.dto.CategoryMapper.toCategoryDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(toCategory(categoryDto));
        log.info(SAVE_MODEL.getMessage(), category);
        return toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));

        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new ValidateException("The category is not empty");
        }
        log.info(DELETE_MODEL.getMessage(), id);
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryById(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        category.setName(categoryDto.getName());
        log.info(UPDATE_MODEL.getMessage(), category);
        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategory(int from, int size) {
        log.info(GET_MODELS.getMessage());
        return categoryRepository.findAll(new PaginationSetup(from, size, Sort.unsorted())).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        log.info(GET_MODEL_BY_ID.getMessage(), catId);
        return toCategoryDto(category);
    }
}