package com.school.entity;

import com.school.enums.LendingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "lendings")
public class Lending {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lendingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", unique = true, nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(nullable = false)
    @Convert(converter = LendingStatus.LendingStatusConverter.class)
    private LendingStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_by")
    private User authorizedBy;

    private LocalDateTime issueDate;
}