package com.school.repository;

import com.school.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsByCategory_CategoryId(Long categoryId);
}
