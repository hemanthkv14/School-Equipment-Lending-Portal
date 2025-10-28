package com.school.repository;

import com.school.entity.Lending;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LendingRepository extends JpaRepository<Lending, Long> {
}
