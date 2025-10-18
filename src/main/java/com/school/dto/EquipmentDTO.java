package com.school.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.entity.Equipment;
import com.school.enums.EquipmentCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class EquipmentDTO {
    private Long equipmentId;

    private String equipmentName;

    private String category;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private EquipmentCondition condition;

    private int quantityTotal;

    private int quantityAvailable;

    private Long addedBy;

    private LocalDateTime createdAt;

    public EquipmentDTO() {
    }

    public EquipmentDTO(Equipment e) {
        this.equipmentId = e.getEquipmentId();
        this.equipmentName = e.getEquipmentName();
        this.category = e.getCategory();
        this.condition = e.getCondition();
        this.quantityTotal = e.getQuantityTotal();
        this.quantityAvailable = e.getQuantityAvailable();
        this.addedBy = e.getAddedBy();
        this.createdAt = e.getCreatedAt();
    }
}
