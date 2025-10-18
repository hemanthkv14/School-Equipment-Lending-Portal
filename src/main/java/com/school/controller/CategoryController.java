package com.school.controller;

import com.school.dto.CategoryDto;
import com.school.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            CategoryDto newCategory = categoryService.addCategory(categoryDto);
            return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getCategoryId() == null || categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            return new ResponseEntity<>("Category ID and new name are required.", HttpStatus.BAD_REQUEST);
        }

        try {
            categoryService.updateCategory(categoryDto);
            return new ResponseEntity<>("Category updated successfully.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>("Category deleted successfully.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Deletion failed. Ensure no equipment is currently assigned to this category.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Deletion failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}