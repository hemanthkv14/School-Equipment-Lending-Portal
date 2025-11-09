package com.school.service;

import com.school.entity.Lending;
import com.school.entity.Notification;
import com.school.entity.User;
import com.school.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for managing notifications within the school system.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Create notifications for lending events (approval, rejection, returns).</li>
 *     <li>Persist notifications in the database for user tracking.</li>
 *     <li>Optional: handle email notifications (currently commented).</li>
 * </ul>
 */
@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Constructs a NotificationService with the given repository.
     *
     * @param notificationRepository repository for {@link Notification} entities
     */
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates and persists a notification for a specific recipient.
     *
     * <p>Logs the creation for traceability.
     *
     * @param recipient the user who will receive the notification
     * @param lending the associated lending record
     * @param type type of notification (e.g., "APPROVED", "REJECTED", "RETURNED")
     * @param message textual content of the notification
     */
    public void createNotification(User recipient, Lending lending, String type, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setLending(lending);
        notification.setType(type);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        log.info("Notification created for user '{}' | lendingId '{}' | type '{}' | message '{}'",
                recipient.getUserId(),
                lending != null ? lending.getLendingId() : null,
                type,
                message);
    }
}