package com.school.entity;

import com.school.dto.EquipmentDTO;
import com.school.enums.EquipmentCondition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Getter
@Setter
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "equipment_name", nullable = false, length = 100)
    private String equipmentName;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    @Convert(converter = EquipmentCondition.EquipmentConditionConverter.class)
    private EquipmentCondition condition;

    @Column(name = "quantity_total", nullable = false)
    private int quantityTotal;

    @Column(name = "quantity_available", nullable = false)
    private int quantityAvailable;

    @Column(name = "added_by", nullable = false)
    private Long addedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static Equipment toEntity(EquipmentDTO equipmentDTO) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentName(equipmentDTO.getEquipmentName());
        equipment.setCategory(equipmentDTO.getCategory());
        equipment.setCondition(equipmentDTO.getCondition());
        equipment.setQuantityTotal(equipmentDTO.getQuantityTotal());
        equipment.setQuantityAvailable(equipmentDTO.getQuantityTotal());
        equipment.setAddedBy(equipmentDTO.getAddedBy());
        equipment.setCreatedAt(LocalDateTime.now());
        return equipment;
    }
}
