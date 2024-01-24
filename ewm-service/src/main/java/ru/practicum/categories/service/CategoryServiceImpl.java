package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.handler.NotFoundException;
import ru.practicum.handler.ValidateException;
import ru.practicum.util.PaginationSetup;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.categories.mapper.CategoryMapper.toCategory;
import static ru.practicum.categories.mapper.CategoryMapper.toCategoryDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(toCategory(categoryDto));
        log.info("Сохранение {}", category);
        return toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", id)));
        if (eventRepository.countByCategoryId(id) > 0) {
            throw new ValidateException("Невозможно удалить категорию со связанными событиями.");
        }
        categoryRepository.delete(category);
        log.info("Удаление по id = {}", id);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", id)));
        category.setName(categoryDto.getName());
        log.info("Обновление {}", category);
        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        log.info("Получение");
        return categoryRepository.findAll(new PaginationSetup(from, size, Sort.unsorted())).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", id)));
        log.info("Получение по id = {}", id);
        return toCategoryDto(category);
    }
}
