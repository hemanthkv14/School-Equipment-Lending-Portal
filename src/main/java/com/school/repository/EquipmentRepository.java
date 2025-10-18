package com.school.repository;

import com.school.entity.Equipment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Equipment e SET e.quantityAvailable = e.quantityAvailable - :quantity " +
            "WHERE e.equipmentId = :equipmentId AND e.quantityAvailable >= :quantity")
    int updateEquipmentQuantityAfterBorrow(Long equipmentId, int quantity);

    @Transactional
    @Modifying
    @Query("UPDATE Equipment e SET e.quantityAvailable = e.quantityAvailable + :quantity " +
            "WHERE e.equipmentId = :equipmentId")
    void updateEquipmentQuantityAfterReturn(Long equipmentId, int quantity);
}
