package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EquipmentDto {
    private Long equipmentId;
    private String name;
    private Long categoryId;
    private Integer totalQuantity;
    private Integer quantityAvailable;
}