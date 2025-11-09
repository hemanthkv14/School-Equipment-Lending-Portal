package com.school.service;

import com.school.dto.EquipmentDto;
import com.school.entity.Category;
import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEquipment_Success() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("Tools");

        EquipmentDto dto = new EquipmentDto();
        dto.setName("Hammer");
        dto.setCategoryId(1L);
        dto.setTotalQuantity(5);

        when(categoryService.getById(1L)).thenReturn(Optional.of(category));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> equipmentService.addEquipment(dto));
        verify(itemService, times(1)).addInitialItems(any(Equipment.class));
    }

    @Test
    void testAddEquipment_CategoryNotFound() {
        EquipmentDto dto = new EquipmentDto();
        dto.setCategoryId(1L);

        when(categoryService.getById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> equipmentService.addEquipment(dto));
    }

    @Test
    void testUpdateEquipment_Success_IncreaseQuantity() {
        Category category = new Category();
        category.setCategoryId(1L);

        Equipment equipment = new Equipment();
        equipment.setEquipmentId(10L);
        equipment.setName("Drill");
        equipment.setTotalQuantity(5);
        equipment.setQuantityAvailable(5);
        equipment.setCategory(category);

        EquipmentDto dto = new EquipmentDto();
        dto.setEquipmentId(10L);
        dto.setTotalQuantity(7);

        when(equipmentRepository.findById(10L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> equipmentService.updateEquipment(dto));
        assertEquals(7, equipment.getTotalQuantity());
        assertEquals(7, equipment.getQuantityAvailable());
        verify(itemService, times(1)).addSpecificNumberOfItems(equipment, 2);
    }

    @Test
    void testUpdateEquipment_DecreaseQuantityThrows() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(10L);
        equipment.setTotalQuantity(5);

        EquipmentDto dto = new EquipmentDto();
        dto.setEquipmentId(10L);
        dto.setTotalQuantity(3);

        when(equipmentRepository.findById(10L)).thenReturn(Optional.of(equipment));

        assertThrows(IllegalArgumentException.class, () -> equipmentService.updateEquipment(dto));
    }

    @Test
    void testDeleteEquipmentCatalog_Success() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setTotalQuantity(0);

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertDoesNotThrow(() -> equipmentService.deleteEquipmentCatalog(1L));
        verify(equipmentRepository, times(1)).delete(equipment);
    }

    @Test
    void testDeleteEquipmentCatalog_WithItemsThrows() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setTotalQuantity(5);

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertThrows(IllegalArgumentException.class, () -> equipmentService.deleteEquipmentCatalog(1L));
    }

    @Test
    void testGetAllEquipment() {
        Equipment e1 = new Equipment();
        e1.setEquipmentId(1L);
        Equipment e2 = new Equipment();
        e2.setEquipmentId(2L);

        when(equipmentRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<EquipmentDto> result = equipmentService.getAllEquipment();
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteItemUnit_Success() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setTotalQuantity(5);
        equipment.setQuantityAvailable(5);

        Item item = new Item();
        item.setItemId(10L);
        item.setEquipment(equipment);
        item.setIsAvailable(true);

        when(itemService.getItemById(10L)).thenReturn(item);
        doNothing().when(itemService).delete(item);
        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        assertDoesNotThrow(() -> equipmentService.deleteItemUnit(10L));
        assertEquals(4, equipment.getTotalQuantity());
        assertEquals(4, equipment.getQuantityAvailable());
    }

    @Test
    void testDeleteItemUnit_OnLoanThrows() {
        Equipment equipment = new Equipment();
        Item item = new Item();
        item.setItemId(10L);
        item.setEquipment(equipment);
        item.setIsAvailable(false);

        when(itemService.getItemById(10L)).thenReturn(item);

        assertThrows(IllegalArgumentException.class, () -> equipmentService.deleteItemUnit(10L));
    }

    @Test
    void testGetEquipmentDtoById_Success() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setName("Saw");

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        EquipmentDto dto = equipmentService.getEquipmentDtoById(1L);
        assertEquals("Saw", dto.getName());
    }

    @Test
    void testGetEquipmentDtoById_NotFound() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> equipmentService.getEquipmentDtoById(1L));
    }
}
