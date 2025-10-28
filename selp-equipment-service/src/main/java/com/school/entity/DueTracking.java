package com.school.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "duetracking")
@Data
@NoArgsConstructor
public class DueTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "due_id")
    private Long dueId;

    @Column(name = "lending_id", unique = true, nullable = false)
    private Long lendingId;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "is_overdue")
    private Boolean overdue;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

}