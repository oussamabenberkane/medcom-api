package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.domain.Alert;
import com.pharmaresolve.medcom.domain.Notification;
import com.pharmaresolve.medcom.service.AlertService;
import com.pharmaresolve.medcom.service.NotificationService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling MailJet webhook callbacks.
 */
@RestController
@RequestMapping("/api/webhooks")
public class MailjetWebhookResource {

    private static final Logger LOG = LoggerFactory.getLogger(MailjetWebhookResource.class);

    private final AlertService alertService;
    private final NotificationService notificationService;

    public MailjetWebhookResource(AlertService alertService, NotificationService notificationService) {
        this.alertService = alertService;
        this.notificationService = notificationService;
    }

    /**
     * Handle MailJet webhook for alert status updates.
     *
     * @param payload the webhook payload from MailJet
     * @return HTTP 200 OK response
     */
    @PostMapping("/alert-status/email")
    public ResponseEntity<String> handleAlertStatusEmail(@RequestBody Map<String, Object> payload) {
        LOG.debug("Received MailJet webhook payload: {}", payload);

        try {
            String event = (String) payload.get("event");
            String messageId = payload.get("MessageID") != null ? payload.get("MessageID").toString() : null;
            Long timestamp = payload.get("time") != null ? ((Number) payload.get("time")).longValue() : null;

            if (messageId == null) {
                LOG.warn("Webhook payload missing MessageID");
                return ResponseEntity.ok("Missing MessageID");
            }

            ZonedDateTime eventTime = timestamp != null ?
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()) :
                ZonedDateTime.now();

            // Try to find notification first (notifications have the actual message ID)
            Optional<Notification> notificationOpt = notificationService.findByMailjetMessageId(messageId);
            if (notificationOpt.isPresent()) {
                updateNotificationStatus(notificationOpt.get(), event, eventTime);
                return ResponseEntity.ok("Notification updated");
            }

            // If not found in notifications, try alerts
            Optional<Alert> alertOpt = alertService.findByMailjetMessageId(messageId);
            if (alertOpt.isPresent()) {
                updateAlertStatus(alertOpt.get(), event, eventTime);
                return ResponseEntity.ok("Alert updated");
            }

            LOG.warn("No Alert or Notification found with MailJet MessageID: {}", messageId);
            return ResponseEntity.ok("Message not found");

        } catch (Exception e) {
            LOG.error("Error processing MailJet webhook", e);
            return ResponseEntity.ok("Error processed");
        }
    }

    private void updateNotificationStatus(Notification notification, String event, ZonedDateTime eventTime) {
        LOG.debug("Updating notification {} with event: {}", notification.getId(), event);

        switch (event) {
            case "sent":
                notification.setSentAt(eventTime);
                break;
            case "delivered":
                notification.setDeliveredAt(eventTime);
                break;
            case "bounce":
            case "blocked":
                LOG.warn("Notification {} failed delivery: {}", notification.getId(), event);
                break;
            default:
                LOG.debug("Unhandled event type: {}", event);
                return;
        }

        notificationService.update(notificationService.findOne(notification.getId()).orElseThrow());
    }

    private void updateAlertStatus(Alert alert, String event, ZonedDateTime eventTime) {
        LOG.debug("Updating alert {} with event: {}", alert.getId(), event);

        switch (event) {
            case "sent":
                alert.setSentAt(eventTime);
                break;
            case "delivered":
                // For alerts, we consider delivered as resolved
                alert.setResolvedAt(eventTime);
                break;
            case "bounce":
            case "blocked":
                LOG.warn("Alert {} failed delivery: {}", alert.getId(), event);
                break;
            default:
                LOG.debug("Unhandled event type: {}", event);
                return;
        }

        alertService.update(alertService.findOne(alert.getId()).orElseThrow());
    }
}