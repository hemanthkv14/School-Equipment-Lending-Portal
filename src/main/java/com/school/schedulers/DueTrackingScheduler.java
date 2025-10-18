package com.school.schedulers;


import com.school.entity.DueTracking;
import com.school.entity.Lending;
import com.school.enums.LendingStatus;
import com.school.repository.DueTrackingRepository;
import com.school.repository.LendingRepository;
import com.school.service.DueTrackingService;
import com.school.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DueTrackingScheduler {

    private final DueTrackingRepository dueTrackingRepository;
    private final LendingRepository lendingRepository;
    private final NotificationService notificationService;
    private final DueTrackingService dueTrackingService; // Injected for targeted query

    public DueTrackingScheduler(DueTrackingRepository dueTrackingRepository,
                                LendingRepository lendingRepository,
                                NotificationService notificationService,
                                DueTrackingService dueTrackingService) {
        this.dueTrackingRepository = dueTrackingRepository;
        this.lendingRepository = lendingRepository;
        this.notificationService = notificationService;
        this.dueTrackingService = dueTrackingService;
    }

    @Scheduled(fixedRate = 43200000)
    @Transactional
    public void runDueTrackingChecks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekOut = now.plusWeeks(1);

        List<DueTracking> activeTracking = dueTrackingService.getActiveAndUpcomingDueTracking(oneWeekOut);

        for (DueTracking tracking : activeTracking) {
            Lending lending = lendingRepository.findById(tracking.getLendingId()).orElse(null);

            if (lending == null || !lending.getApprovalStatus().equals(LendingStatus.APPROVED)) {
                continue;
            }

            if (tracking.getDueDate().isBefore(now) && !tracking.getIsOverdue()) {
                tracking.setIsOverdue(true);
                dueTrackingRepository.save(tracking);

                notificationService.createNotification(lending.getBorrower(), lending, "OVERDUE",
                        "ALERT: Your loan for " + lending.getItem().getEquipment().getName() + " is now overdue. Please return immediately.");
            } else if (tracking.getDueDate().isAfter(now)) {
                notificationService.createNotification(lending.getBorrower(), lending, "REMINDER",
                        "REMINDER: Your loan for " + lending.getItem().getEquipment().getName() + " is due on " + tracking.getDueDate().toLocalDate());
            }
        }
    }
}