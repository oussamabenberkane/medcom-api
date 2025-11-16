package com.oussamabenberkane.medcom.service.impl;

import com.oussamabenberkane.medcom.domain.Notification;
import com.oussamabenberkane.medcom.repository.NotificationRepository;
import com.oussamabenberkane.medcom.service.MailService;
import com.oussamabenberkane.medcom.service.NotificationService;
import com.oussamabenberkane.medcom.service.dto.NotificationDTO;
import com.oussamabenberkane.medcom.service.mapper.NotificationMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.oussamabenberkane.medcom.domain.Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final MailService mailService;

    public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        MailService mailService
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.mailService = mailService;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    /**
     * Send pending notification emails.
     * Scheduled to run every 2 minutes.
     */
    @Scheduled(fixedDelay = 120000) // Run every 2 minutes
    @Transactional
    public void sendPendingNotificationEmails() {
        LOG.debug("Processing pending notification emails");

        List<Notification> pendingNotifications = notificationRepository.findAllByEmailSentFalse();
        LOG.info("Found {} pending notifications to send", pendingNotifications.size());

        for (Notification notification : pendingNotifications) {
            try {
                sendNotificationEmail(notification);
            } catch (Exception e) {
                LOG.error("Error sending notification {}: {}", notification.getId(), e.getMessage());
                // Mark as failed
                notification.setEmailFailed(true);
                notification.setEmailFailedAt(Instant.now());
                notification.setEmailFailureReason(e.getMessage());
                notificationRepository.save(notification);
            }
        }
    }

    /**
     * Send email for a single notification.
     *
     * @param notification The notification to send
     */
    private void sendNotificationEmail(Notification notification) {
        if (notification.getUser() == null || notification.getUser().getEmail() == null) {
            LOG.warn("Cannot send notification {} - user or email is null", notification.getId());
            notification.setEmailFailed(true);
            notification.setEmailFailedAt(Instant.now());
            notification.setEmailFailureReason("User or email is null");
            notificationRepository.save(notification);
            return;
        }

        if (notification.getWatchlistItem() == null || notification.getWatchlistItem().getProduct() == null) {
            LOG.warn("Cannot send notification {} - watchlist item or product is null", notification.getId());
            notification.setEmailFailed(true);
            notification.setEmailFailedAt(Instant.now());
            notification.setEmailFailureReason("Watchlist item or product is null");
            notificationRepository.save(notification);
            return;
        }

        try {
            String messageId = mailService.sendAlertEmail(
                notification.getUser(),
                notification.getWatchlistItem().getProduct(),
                notification.getWatchlistItem().getLastAvailabilityStatus(),
                notification.getPharmacy()
            );

            notification.setEmailSent(true);
            notification.setEmailSentAt(Instant.now());
            notification.setMailjetMessageId(messageId);

            // Assume delivery is successful for now
            // In a real implementation, you'd track this via webhooks
            notification.setEmailDelivered(true);
            notification.setEmailDeliveredAt(Instant.now());

            notificationRepository.save(notification);
            LOG.info("Successfully sent notification {} to {}", notification.getId(), notification.getUser().getEmail());
        } catch (Exception e) {
            LOG.error("Failed to send notification {} to {}: {}", notification.getId(), notification.getUser().getEmail(), e.getMessage());
            throw e;
        }
    }
}
