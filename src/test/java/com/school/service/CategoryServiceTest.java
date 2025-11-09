package com.school.service;

import com.school.dto.CategoryDto;
import com.school.entity.Category;
import com.school.repository.CategoryRepository;
import com.school.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCategory_Success() {
        CategoryDto dto = new CategoryDto(2L, "Electronics");
        Category saved = new Category();
        saved.setCategoryId(1L);
        saved.setName("Electronics");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDto result = categoryService.addCategory(dto);

        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Electronics", result.getName());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testGetAllCategories() {
        Category cat1 = new Category(1L, "Electronics");
        Category cat2 = new Category(2L, "Books");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));

        List<CategoryDto> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        assertEquals("Electronics", categories.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCategory_Success() {
        Category existing = new Category(1L, "Old Name");
        CategoryDto updateDto = new CategoryDto(1L, "New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        assertDoesNotThrow(() -> categoryService.updateCategory(updateDto));
        assertEquals("New Name", existing.getName());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateCategory_MissingId() {
        CategoryDto dto = new CategoryDto(null, "New Name");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(dto));
        assertEquals("Category ID is required for update.", ex.getMessage());
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryDto dto = new CategoryDto(1L, "New Name");
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(dto));
        assertEquals("Category not found with ID: 1", ex.getMessage());
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(equipmentRepository.existsByCategory_CategoryId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(1L));
        assertEquals("Category not found with ID: 1", ex.getMessage());
    }

    @Test
    void testDeleteCategory_WithAssignedEquipment() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(equipmentRepository.existsByCategory_CategoryId(1L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(1L));
        assertTrue(ex.getMessage().contains("Cannot delete category ID 1 because equipment is currently assigned to it."));
    }

    @Test
    void testGetById_Found() {
        Category cat = new Category(1L, "Electronics");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        Optional<Category> result = categoryService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
    }

    @Test
    void testGetById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getById(1L);
        assertTrue(result.isEmpty());
    }
}
