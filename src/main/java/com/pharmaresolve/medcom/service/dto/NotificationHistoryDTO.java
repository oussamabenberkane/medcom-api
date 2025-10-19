package com.pharmaresolve.medcom.service.dto;

import com.pharmaresolve.medcom.domain.enumeration.NotificationStatus;
import com.pharmaresolve.medcom.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A DTO for Notification history with embedded alert and product information.
 */
public class NotificationHistoryDTO implements Serializable {

    private Long id;
    private NotificationType type;
    private String content;
    private ZonedDateTime sentAt;
    private ZonedDateTime deliveredAt;
    private NotificationStatus status;
    private String errorMessage;
    private String recipientEmail;
    private String recipientName;
    private ZonedDateTime readAt;

    // Alert information
    private Long alertId;
    private String alertMessage;
    private ZonedDateTime alertCreated;

    // Product information from watchlist item
    private Long productId;
    private String productName;
    private Boolean availabilityStatus;
    private Integer priority;

    // Watchlist item information
    private Long watchlistItemId;

    public NotificationHistoryDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(ZonedDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public ZonedDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(ZonedDateTime readAt) {
        this.readAt = readAt;
    }

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public ZonedDateTime getAlertCreated() {
        return alertCreated;
    }

    public void setAlertCreated(ZonedDateTime alertCreated) {
        this.alertCreated = alertCreated;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(Boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getWatchlistItemId() {
        return watchlistItemId;
    }

    public void setWatchlistItemId(Long watchlistItemId) {
        this.watchlistItemId = watchlistItemId;
    }

    @Override
    public String toString() {
        return "NotificationHistoryDTO{" +
            "id=" + id +
            ", type=" + type +
            ", content='" + content + '\'' +
            ", sentAt=" + sentAt +
            ", deliveredAt=" + deliveredAt +
            ", status=" + status +
            ", errorMessage='" + errorMessage + '\'' +
            ", recipientEmail='" + recipientEmail + '\'' +
            ", recipientName='" + recipientName + '\'' +
            ", readAt=" + readAt +
            ", alertId=" + alertId +
            ", alertMessage='" + alertMessage + '\'' +
            ", alertCreated=" + alertCreated +
            ", productId=" + productId +
            ", productName='" + productName + '\'' +
            ", availabilityStatus=" + availabilityStatus +
            ", priority=" + priority +
            ", watchlistItemId=" + watchlistItemId +
            '}';
    }
}
