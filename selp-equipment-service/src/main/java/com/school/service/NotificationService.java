package com.school.service;

import com.school.entity.Lending;
import com.school.entity.Notification;
import com.school.entity.User;
import com.school.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User recipient, Lending lending, String type, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setLending(lending);
        notification.setType(type);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}

//    @Transactional
//    public void sendNotificationMail(String toEmail, String subject, String body) {
//        final String fromEmail = "your_email@gmail.com";
//        final String password = "your_app_password"; // use App Password if 2FA enabled
//
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587"); // TLS port
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//
//        // Create session
//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(fromEmail, password);
//            }
//        });
//
//        try {
//            // Create message
//            Message msg = new MimeMessage(session);
//            msg.setFrom(new InternetAddress(fromEmail));
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//            msg.setSubject(subject);
//            msg.setText(body);
//
//            // Send email
//            Transport.send(msg);
//            System.out.println("Email sent successfully!");
//
//        } catch (MessagingException ignored) {
//        }
//    }
