package com.school.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "due_tracking")
public class DueTracking {
    @Id
    @Column(name = "lending_id")
    private Long lendingId;

    @OneToOne
    @MapsId // Maps the primary key to the foreign key from Lending
    @JoinColumn(name = "lending_id")
    private Lending lending;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    private Boolean isOverdue = false;
}