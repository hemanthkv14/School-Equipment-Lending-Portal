package com.school.controller;

import com.school.dto.CategoryDto;
import com.school.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing categories.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Provide endpoints for creating, retrieving, updating, and deleting categories.</li>
 *     <li>Handle HTTP response codes and error messages for client requests.</li>
 * </ul>
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Adds a new category.
     *
     * @param categoryDto The category data to add
     * @return ResponseEntity with the created {@link CategoryDto} and HTTP status
     */
    @PostMapping("/add")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            log.warn("Attempted to add category with empty name.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            CategoryDto newCategory = categoryService.addCategory(categoryDto);
            log.info("Category added successfully with ID: {} | Name: {}", newCategory.getCategoryId(), newCategory.getName());
            return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to add category: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    /**
     * Retrieves all categories.
     *
     * @return ResponseEntity containing a list of {@link CategoryDto} objects with HTTP status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        log.debug("Retrieved {} categories.", categories.size());
        return ResponseEntity.ok(categories);
    }

    /**
     * Updates an existing category.
     *
     * @param categoryDto The category data containing ID and new name
     * @return ResponseEntity with a message and HTTP status
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getCategoryId() == null || categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            log.warn("Update failed. Category ID or name missing.");
            return new ResponseEntity<>("Category ID and new name are required.", HttpStatus.BAD_REQUEST);
        }

        try {
            categoryService.updateCategory(categoryDto);
            log.info("Category updated successfully with ID: {}", categoryDto.getCategoryId());
            return new ResponseEntity<>("Category updated successfully.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error("Category update failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Category update failed: {}", e.getMessage());
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId The ID of the category to delete
     * @return ResponseEntity with a message and HTTP status
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            log.info("Category deleted successfully with ID: {}", categoryId);
            return new ResponseEntity<>("Category deleted successfully.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.warn("Category deletion failed. Not found: {}", categoryId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            log.warn("Category deletion failed due to integrity constraints: {}", categoryId);
            return new ResponseEntity<>("Deletion failed. Ensure no equipment is currently assigned to this category.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Category deletion failed for ID {}: {}", categoryId, e.getMessage());
            return new ResponseEntity<>("Deletion failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}