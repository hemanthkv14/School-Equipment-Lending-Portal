package com.school.service;

import com.school.dto.CategoryDto;
import com.school.entity.Category;
import com.school.repository.CategoryRepository;
import com.school.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ============================================================
 * 🧩 CategoryService
 * ============================================================
 * Service layer for managing {@link Category} entities and their
 * Data Transfer Object (DTO) representations.
 *
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Convert between {@link Category} and {@link CategoryDto}.</li>
 *   <li>Handle CRUD operations for categories via {@link CategoryRepository}.</li>
 *   <li>Enforce business rules related to deletion when equipment is assigned via {@link EquipmentRepository}.</li>
 * </ul>
 *
 * <p>Includes logging for important operations and exceptions.</p>
 * ============================================================
 */
@Slf4j
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

    /**
     * Add a new category.
     *
     * <p>The provided DTO is converted to an entity and saved.
     * The saved entity is then converted back to a DTO and returned.</p>
     *
     * @param dto category data transfer object containing details to create
     * @return the saved {@link CategoryDto} with generated id (if any)
     */
    public CategoryDto addCategory(CategoryDto dto) {
        log.info("Adding new category: {}", dto.getName());
        Category category = convertToEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID={}", savedCategory.getCategoryId());
        return convertToDto(savedCategory);
    }

    public List<CategoryDto> getAllCategories() {
        log.info("Fetching all categories...");
        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        log.info("Retrieved {} categories.", categories.size());
        return categories;
    }

    /**
     * Update an existing category.
     *
     * <p>Only non-blank name values from the provided DTO are applied
     * to the existing entity.</p>
     *
     * @param dto DTO containing the category id and fields to update
     * @throws IllegalArgumentException if {@code dto.getCategoryId()} is {@code null}
     * @throws EntityNotFoundException if no category exists with the provided id
     */
    public void updateCategory(CategoryDto dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required for update.");
        }
        log.info("Updating category with ID={}", dto.getCategoryId());

        Category existingCategory = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            existingCategory.setName(dto.getName());
        }

        categoryRepository.save(existingCategory);
        log.info("Category ID={} updated successfully.", dto.getCategoryId());
    }

    /**
     * Delete a category by id.
     *
     * <p>Deletion is prevented if the category does not exist or if there is
     * equipment assigned to it.</p>
     *
     * @param categoryId id of the category to delete
     * @throws EntityNotFoundException if no category exists with the provided id
     * @throws IllegalStateException if equipment is currently assigned to the category
     */
    public void deleteCategory(Long categoryId) {
        log.info("Attempting to delete category with ID={}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            log.warn("Category not found with ID={}", categoryId);
            throw new EntityNotFoundException("Category not found with ID: " + categoryId);
        }

        if (equipmentRepository.existsByCategory_CategoryId(categoryId)) {
            log.error("Cannot delete category ID={} because equipment is assigned.", categoryId);
            throw new IllegalStateException("Cannot delete category ID " + categoryId + " because equipment is currently assigned to it.");
        }

        categoryRepository.deleteById(categoryId);
        log.info("Category ID={} deleted successfully.", categoryId);
    }

    /**
     * Retrieve a category entity by id.
     *
     * <p>Note: this returns the entity type rather than a DTO to allow callers
     * to perform additional domain operations before mapping to a DTO if needed.</p>
     *
     * @param id category id to look up
     * @return {@link Optional} containing the found {@link Category} or empty if not found
     */
    public Optional<Category> getById(Long id) {
        log.debug("Fetching category by ID={}", id);
        return categoryRepository.findById(id);
    }
}