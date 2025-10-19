package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Alert;
import com.pharmaresolve.medcom.domain.Notification;
import com.pharmaresolve.medcom.domain.User;
import com.pharmaresolve.medcom.domain.enumeration.AlertStatus;
import com.pharmaresolve.medcom.domain.enumeration.NotificationType;
import com.pharmaresolve.medcom.domain.enumeration.NotificationStatus;
import com.pharmaresolve.medcom.repository.NotificationRepository;
import com.pharmaresolve.medcom.repository.UserRepository;
import com.pharmaresolve.medcom.service.dto.NotificationDTO;
import com.pharmaresolve.medcom.service.dto.NotificationHistoryDTO;
import com.pharmaresolve.medcom.service.mapper.NotificationMapper;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.pharmaresolve.medcom.domain.Notification}.
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final UserRepository userRepository;

    private final EmailDeliveryService emailDeliveryService;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper, UserRepository userRepository, EmailDeliveryService emailDeliveryService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userRepository = userRepository;
        this.emailDeliveryService = emailDeliveryService;
    }

    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    /**
     * Update a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    /**
     * Get all the notifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Notifications");
        return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
    }

    /**
     * Get one notification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    /**
     * Delete the notification by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    /**
     * Create email notifications for all pharmacy users when an alert is created.
     * Also triggers email sending immediately after creating notifications.
     *
     * @param alert the alert for which to create notifications.
     * @return the list of created notifications.
     */
    public List<NotificationDTO> createEmailNotificationsForAlert(Alert alert) {
        LOG.debug("Request to create email notifications for Alert : {}", alert.getId());

        Long pharmacyId = alert.getWatchlistItem().getWatchlist().getPharmacy().getId();
        List<String> pharmacyUserRoles = List.of("ROLE_USER");
        List<User> pharmacyUsers = userRepository.findByPharmacyIdAndActivatedIsTrueAndAuthorities_NameIn(pharmacyId, pharmacyUserRoles);

        // Create notifications for each pharmacy user
        List<Notification> createdNotifications = pharmacyUsers.stream()
            .map(user -> {
                Notification notification = new Notification()
                    .type(NotificationType.EMAIL)
                    .content(alert.getMessage())
                    .status(NotificationStatus.PENDING)
                    .recipientEmail(user.getEmail())
                    .recipientName(user.getFirstName() + " " + user.getLastName())
                    .alert(alert);

                notification = notificationRepository.save(notification);

                LOG.debug("Created notification {} for user {} ({})",
                    notification.getId(), user.getLogin(), user.getEmail());

                return notification;
            })
            .toList();

        LOG.info("Created {} notifications for alert {}, triggering email delivery",
            createdNotifications.size(), alert.getId());

        // Trigger email sending asynchronously
        if (!createdNotifications.isEmpty()) {
            emailDeliveryService.sendNotifications(createdNotifications);
        }

        // Convert to DTOs for return
        return createdNotifications.stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    /**
     * Create consolidated email notifications for multiple alerts grouped by pharmacy.
     * This sends one email per pharmacy containing all alerts from the current scheduler run.
     *
     * @param alerts the list of alerts to create notifications for
     */
    @Async
    public void createConsolidatedEmailNotifications(List<Alert> alerts) {
        LOG.debug("Request to create consolidated email notifications for {} alerts", alerts.size());

        // Group alerts by pharmacy
        Map<Long, List<Alert>> alertsByPharmacy = alerts.stream()
            .collect(Collectors.groupingBy(alert ->
                alert.getWatchlistItem().getWatchlist().getPharmacy().getId()));

        LOG.info("Grouped {} alerts into {} pharmacies for consolidated notifications",
            alerts.size(), alertsByPharmacy.size());

        for (Map.Entry<Long, List<Alert>> entry : alertsByPharmacy.entrySet()) {
            Long pharmacyId = entry.getKey();
            List<Alert> pharmacyAlerts = entry.getValue();

            try {
                createConsolidatedEmailForPharmacy(pharmacyId, pharmacyAlerts);
            } catch (Exception e) {
                LOG.error("Failed to create consolidated email for pharmacy {}: {}", pharmacyId, e.getMessage(), e);
            }
        }
    }

    /**
     * Create consolidated email notifications for a specific pharmacy.
     * Creates exactly 1 notification per user containing ALL alerts for that pharmacy.
     *
     * @param pharmacyId the pharmacy ID
     * @param alerts the alerts for this pharmacy
     */
    private void createConsolidatedEmailForPharmacy(Long pharmacyId, List<Alert> alerts) {
        LOG.debug("Creating consolidated email for pharmacy {} with {} alerts", pharmacyId, alerts.size());

        List<String> pharmacyUserRoles = List.of("ROLE_USER");
        List<User> pharmacyUsers = userRepository.findByPharmacyIdAndActivatedIsTrueAndAuthorities_NameIn(pharmacyId, pharmacyUserRoles);

        if (pharmacyUsers.isEmpty()) {
            LOG.warn("No active users found for pharmacy {}, skipping consolidated email", pharmacyId);
            return;
        }

        // Create ONE notification per user for ALL alerts in this pharmacy
        List<Notification> createdNotifications = pharmacyUsers.stream()
            .map(user -> {
                Notification notification = new Notification()
                    .type(NotificationType.EMAIL)
                    .content(String.format("Product availability updates: %d products changed", alerts.size()))
                    .status(NotificationStatus.PENDING)
                    .recipientEmail(user.getEmail())
                    .recipientName(user.getFirstName() + " " + user.getLastName())
                    .alert(null); // No single alert - this notification represents multiple alerts

                notification = notificationRepository.save(notification);

                LOG.debug("Created consolidated notification {} for user {} ({}) containing {} alerts",
                    notification.getId(), user.getLogin(), user.getEmail(), alerts.size());

                return notification;
            })
            .toList();

        LOG.info("Created {} consolidated notifications for pharmacy {} (1 per user), triggering email delivery",
            createdNotifications.size(), pharmacyId);

        // Trigger consolidated email sending asynchronously
        // Each notification will receive the SAME list of alerts for this pharmacy
        if (!createdNotifications.isEmpty()) {
            emailDeliveryService.sendConsolidatedNotifications(createdNotifications, alerts);
        }

        // Mark all alerts as processed (they'll be marked as SENT when emails are successfully sent)
        alerts.forEach(alert -> {
            alert.setStatus(AlertStatus.PENDING);
            // Will be saved as SENT when email delivery succeeds
        });
    }

    /**
     * Find notification by MailJet message ID.
     *
     * @param mailjetMessageId the MailJet message ID.
     * @return the notification if found.
     */
    @Transactional(readOnly = true)
    public Optional<Notification> findByMailjetMessageId(String mailjetMessageId) {
        LOG.debug("Request to find Notification by MailJet message ID : {}", mailjetMessageId);
        return Optional.ofNullable(notificationRepository.findByMailjetMessageId(mailjetMessageId));
    }

    /**
     * Get notification history for a user with optional filtering.
     *
     * @param userEmail the user's email
     * @param status optional status filter
     * @param type optional type filter
     * @param pageable pagination information
     * @return page of notification history DTOs
     */
    @Transactional(readOnly = true)
    public Page<NotificationHistoryDTO> getNotificationHistoryForUser(
        String userEmail,
        NotificationStatus status,
        NotificationType type,
        Pageable pageable
    ) {
        LOG.debug("Request to get notification history for user: {} with filters - status: {}, type: {}",
            userEmail, status, type);

        Page<Notification> notifications = notificationRepository.findByRecipientEmailWithFilters(
            userEmail, status, type, pageable
        );

        return notifications.map(this::convertToHistoryDTO);
    }

    /**
     * Get notification history for a user for a specific watchlist item with optional filtering.
     *
     * @param userEmail the user's email
     * @param watchlistItemId the watchlist item ID
     * @param status optional status filter
     * @param type optional type filter
     * @param pageable pagination information
     * @return page of notification history DTOs
     */
    @Transactional(readOnly = true)
    public Page<NotificationHistoryDTO> getNotificationHistoryForUserAndItem(
        String userEmail,
        Long watchlistItemId,
        NotificationStatus status,
        NotificationType type,
        Pageable pageable
    ) {
        LOG.debug("Request to get notification history for user: {} and watchlist item: {} with filters - status: {}, type: {}",
            userEmail, watchlistItemId, status, type);

        Page<Notification> notifications = notificationRepository.findByRecipientEmailAndWatchlistItemWithFilters(
            userEmail, watchlistItemId, status, type, pageable
        );

        return notifications.map(this::convertToHistoryDTO);
    }

    /**
     * Mark a notification as read.
     *
     * @param notificationId the notification ID
     * @param userEmail the user's email (for authorization)
     * @return the updated notification DTO
     */
    public Optional<NotificationDTO> markAsRead(Long notificationId, String userEmail) {
        LOG.debug("Request to mark notification {} as read for user: {}", notificationId, userEmail);

        Optional<Notification> notificationOpt = notificationRepository.findByIdAndRecipientEmail(notificationId, userEmail);

        if (notificationOpt.isEmpty()) {
            LOG.warn("Notification {} not found or user {} does not have access", notificationId, userEmail);
            return Optional.empty();
        }

        Notification notification = notificationOpt.get();

        if (notification.getReadAt() != null) {
            LOG.debug("Notification {} is already marked as read at {}", notificationId, notification.getReadAt());
            return Optional.of(notificationMapper.toDto(notification));
        }

        notification.setReadAt(ZonedDateTime.now());
        notification = notificationRepository.save(notification);

        LOG.info("Notification {} marked as read for user {}", notificationId, userEmail);

        return Optional.of(notificationMapper.toDto(notification));
    }

    /**
     * Convert a Notification entity to NotificationHistoryDTO with alert and product information.
     *
     * @param notification the notification entity
     * @return the notification history DTO
     */
    private NotificationHistoryDTO convertToHistoryDTO(Notification notification) {
        NotificationHistoryDTO dto = new NotificationHistoryDTO();

        // Basic notification fields
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setContent(notification.getContent());
        dto.setSentAt(notification.getSentAt());
        dto.setDeliveredAt(notification.getDeliveredAt());
        dto.setStatus(notification.getStatus());
        dto.setErrorMessage(notification.getErrorMessage());
        dto.setRecipientEmail(notification.getRecipientEmail());
        dto.setRecipientName(notification.getRecipientName());
        dto.setReadAt(notification.getReadAt());

        // Alert information (may be null for consolidated notifications)
        Alert alert = notification.getAlert();
        if (alert != null) {
            dto.setAlertId(alert.getId());
            dto.setAlertMessage(alert.getMessage());
            dto.setAlertCreated(alert.getCreated());

            // Product information from watchlist item
            if (alert.getWatchlistItem() != null) {
                dto.setWatchlistItemId(alert.getWatchlistItem().getId());
                dto.setAvailabilityStatus(alert.getWatchlistItem().getLastAvailabilityStatus());
                dto.setPriority(alert.getWatchlistItem().getPriority());

                if (alert.getWatchlistItem().getProduct() != null) {
                    dto.setProductId(alert.getWatchlistItem().getProduct().getId());
                    dto.setProductName(alert.getWatchlistItem().getProduct().getName());
                }
            }
        }

        return dto;
    }
}
