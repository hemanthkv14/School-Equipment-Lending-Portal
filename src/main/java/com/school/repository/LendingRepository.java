package com.school.repository;

import com.school.entity.Lending;
import com.school.enums.LendingStatus; // Assuming your enum is here
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

@Repository
public interface LendingRepository extends JpaRepository<Lending, Long> {
    Optional<Lending> findByItemItemIdAndApprovalStatusIn(Long itemId, List<LendingStatus> statusList);

    Optional<Lending> findByLendingIdAndApprovalStatusIn(Long lendingId, List<LendingStatus> statusList);

    List<Lending> findByBorrowerUserId(Long userId);

}