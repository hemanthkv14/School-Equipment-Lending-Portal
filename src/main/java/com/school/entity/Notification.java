package com.school.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lending_id")
    private Lending lending;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean notificationSent = false;

    private LocalDateTime sentAt;
}