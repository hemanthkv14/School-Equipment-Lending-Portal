package com.school.service;

import com.school.entity.Lending;
import com.school.entity.Notification;
import com.school.entity.User;
import com.school.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    void testCreateNotification() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");

        Lending lending = new Lending();
        lending.setLendingId(100L);

        String type = "APPROVED";
        String message = "Your request has been approved.";

        notificationService.createNotification(user, lending, type, message);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(user, saved.getRecipient());
        assertEquals(lending, saved.getLending());
        assertEquals(type, saved.getType());
        assertEquals(message, saved.getMessage());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testCreateNotificationWithNullLending() {
        User user = new User();
        user.setUserId(1L);

        String type = "INFO";
        String message = "General notification";

        notificationService.createNotification(user, null, type, message);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(user, saved.getRecipient());
        assertNull(saved.getLending());
        assertEquals(type, saved.getType());
        assertEquals(message, saved.getMessage());
        assertNotNull(saved.getCreatedAt());
    }
}
