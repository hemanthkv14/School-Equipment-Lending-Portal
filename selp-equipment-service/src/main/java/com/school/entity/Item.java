package com.school.entity;

import com.school.enums.ItemCondition;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(unique = true)
    private String serialNumber;

    @Column(nullable = false)
    @Convert(converter = ItemCondition.ItemConditionConverter.class)
    private ItemCondition condition;

    @Column(nullable = false)
    private Boolean isAvailable = true;
}