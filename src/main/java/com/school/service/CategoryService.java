package com.school.service;

import com.school.dto.CategoryDto;
import com.school.entity.Category;
import com.school.repository.CategoryRepository;
import com.school.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;

    public CategoryService(CategoryRepository categoryRepository, EquipmentRepository equipmentRepository) {
        this.categoryRepository = categoryRepository;
        this.equipmentRepository = equipmentRepository;
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        return dto;
    }

    private Category convertToEntity(CategoryDto dto) {
        Category entity = new Category();
        entity.setCategoryId(dto.getCategoryId());
        entity.setName(dto.getName());
        return entity;
    }

    public CategoryDto addCategory(CategoryDto dto) {
        Category category = convertToEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void updateCategory(CategoryDto dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required for update.");
        }
        Category existingCategory = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            existingCategory.setName(dto.getName());
        }
        categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found with ID: " + categoryId);
        }

        if (equipmentRepository.existsByCategory_CategoryId(categoryId)) {
            throw new IllegalStateException("Cannot delete category ID " + categoryId + " because equipment is currently assigned to it.");
        }

        categoryRepository.deleteById(categoryId);
    }

    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }
}