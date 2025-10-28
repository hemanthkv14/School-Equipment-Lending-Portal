package com.school.dto;

import com.school.enums.ItemCondition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDto {

    private Long itemId;
    private Long equipmentId;
    private String equipmentName;
    private String serialNumber;
    private ItemCondition condition;
    private Boolean isAvailable;
}