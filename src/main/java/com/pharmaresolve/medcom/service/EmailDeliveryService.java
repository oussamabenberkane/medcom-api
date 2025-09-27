package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Notification;
import com.pharmaresolve.medcom.domain.enumeration.NotificationStatus;
import com.pharmaresolve.medcom.repository.NotificationRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for delivering email notifications.
 * Bridges the gap between notification creation and actual email sending.
 */
@Service
@Transactional
public class EmailDeliveryService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailDeliveryService.class);

    private final NotificationRepository notificationRepository;
    private final MailService mailService;
    private final UserService userService;

    public EmailDeliveryService(
        NotificationRepository notificationRepository,
        MailService mailService,
        UserService userService
    ) {
        this.notificationRepository = notificationRepository;
        this.mailService = mailService;
        this.userService = userService;
    }

    /**
     * Send email notifications asynchronously (semi-sync approach).
     * Waits for email sending result to update notification status.
     *
     * @param notifications list of notifications to send
     */
    @Async
    public void sendNotifications(List<Notification> notifications) {
        LOG.info("Starting email delivery for {} notifications", notifications.size());

        for (Notification notification : notifications) {
            try {
                sendSingleNotification(notification);
            } catch (Exception e) {
                LOG.error("Failed to process notification {}: {}", notification.getId(), e.getMessage(), e);
                markNotificationAsFailed(notification, e.getMessage());
            }
        }

        LOG.info("Completed email delivery processing for {} notifications", notifications.size());
    }

    /**
     * Send a single notification email.
     *
     * @param notification the notification to send
     */
    private void sendSingleNotification(Notification notification) {
        LOG.debug("Processing notification: {}", notification.getId());

        // Find the user associated with this notification
        // We need to get the user based on the pharmacy and notification context
        // For now, we'll extract the user email from the notification content or alert context

        String userEmail = extractUserEmailFromNotification(notification);
        String userName = extractUserNameFromNotification(notification);
        if (userEmail == null) {
            LOG.warn("Cannot determine user email for notification: {}", notification.getId());
            markNotificationAsFailed(notification, "User email not found");
            return;
        }

        try {
            // Update status to indicate sending is in progress
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(ZonedDateTime.now());
            notificationRepository.save(notification);

            // Send the email using MailService
            sendAlertEmail(notification, userEmail, userName);

            LOG.debug("Email sent successfully for notification: {}", notification.getId());

        } catch (Exception e) {
            LOG.error("Failed to send email for notification {}: {}", notification.getId(), e.getMessage());
            markNotificationAsFailed(notification, e.getMessage());
        }
    }

    /**
     * Send alert email using the existing MailService.
     *
     * @param notification the notification containing the alert
     * @param userEmail recipient email address
     * @param userName recipient name
     */
    private void sendAlertEmail(Notification notification, String userEmail, String userName) {
        // Use the existing alertEmail template from MailService
        // We need to pass the required context variables

        if (notification.getAlert() != null && notification.getAlert().getWatchlistItem() != null) {
            // Call the MailService to send the alert email
            // This will be updated when we modify MailService to accept Notification entities
            mailService.sendAlertEmail(
                userEmail,
                userName,
                notification.getAlert(),
                notification.getAlert().getWatchlistItem()
            );

            // If we reach here, email was sent successfully
            // MailJet message ID will be updated via webhook if needed

        } else {
            throw new IllegalStateException("Notification does not have valid alert or watchlist item");
        }
    }

    /**
     * Extract user email from notification.
     *
     * @param notification the notification
     * @return user email or null if not found
     */
    private String extractUserEmailFromNotification(Notification notification) {
        return notification.getRecipientEmail();
    }

    /**
     * Extract user email from notification.
     *
     * @param notification the notification
     * @return user email or null if not found
     */
    private String extractUserNameFromNotification(Notification notification) {
        return notification.getRecipientName();
    }

    /**
     * Mark notification as failed with error message.
     *
     * @param notification the notification that failed
     * @param errorMessage the error message
     */
    private void markNotificationAsFailed(Notification notification, String errorMessage) {
        try {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(errorMessage);
            notificationRepository.save(notification);
            LOG.debug("Marked notification {} as FAILED: {}", notification.getId(), errorMessage);
        } catch (Exception e) {
            LOG.error("Failed to update notification status for {}: {}", notification.getId(), e.getMessage());
        }
    }

    /**
     * Update notification status when email is delivered (called from webhook).
     *
     * @param mailjetMessageId the MailJet message ID
     * @param delivered true if delivered, false if failed
     */
    public void updateDeliveryStatus(String mailjetMessageId, boolean delivered) {
        LOG.debug("Updating delivery status for message: {}, delivered: {}", mailjetMessageId, delivered);

        Notification notification = notificationRepository.findByMailjetMessageId(mailjetMessageId);
        if (notification != null) {
            if (delivered) {
                notification.setStatus(NotificationStatus.DELIVERED);
                notification.setDeliveredAt(ZonedDateTime.now());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Email delivery failed");
            }
            notificationRepository.save(notification);
            LOG.debug("Updated notification {} status to: {}", notification.getId(), notification.getStatus());
        } else {
            LOG.warn("No notification found for MailJet message ID: {}", mailjetMessageId);
        }
    }
}
