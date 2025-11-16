package com.oussamabenberkane.medcom.service.dto;

import com.oussamabenberkane.medcom.domain.enumeration.NotificationType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.oussamabenberkane.medcom.domain.Notification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    @NotNull
    private NotificationType notificationType;

    @Size(max = 1000)
    private String message;

    private Boolean emailSent;

    private Instant emailSentAt;

    private Boolean emailDelivered;

    private Instant emailDeliveredAt;

    private Boolean emailFailed;

    private Instant emailFailedAt;

    @Size(max = 500)
    private String emailFailureReason;

    @Size(max = 255)
    private String mailjetMessageId;

    private Instant readAt;

    private WatchlistDTO watchlist;

    private UserDTO user;

    private PharmacyDTO pharmacy;

    private WatchlistItemDTO watchlistItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public Instant getEmailSentAt() {
        return emailSentAt;
    }

    public void setEmailSentAt(Instant emailSentAt) {
        this.emailSentAt = emailSentAt;
    }

    public Boolean getEmailDelivered() {
        return emailDelivered;
    }

    public void setEmailDelivered(Boolean emailDelivered) {
        this.emailDelivered = emailDelivered;
    }

    public Instant getEmailDeliveredAt() {
        return emailDeliveredAt;
    }

    public void setEmailDeliveredAt(Instant emailDeliveredAt) {
        this.emailDeliveredAt = emailDeliveredAt;
    }

    public Boolean getEmailFailed() {
        return emailFailed;
    }

    public void setEmailFailed(Boolean emailFailed) {
        this.emailFailed = emailFailed;
    }

    public Instant getEmailFailedAt() {
        return emailFailedAt;
    }

    public void setEmailFailedAt(Instant emailFailedAt) {
        this.emailFailedAt = emailFailedAt;
    }

    public String getEmailFailureReason() {
        return emailFailureReason;
    }

    public void setEmailFailureReason(String emailFailureReason) {
        this.emailFailureReason = emailFailureReason;
    }

    public String getMailjetMessageId() {
        return mailjetMessageId;
    }

    public void setMailjetMessageId(String mailjetMessageId) {
        this.mailjetMessageId = mailjetMessageId;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public WatchlistDTO getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(WatchlistDTO watchlist) {
        this.watchlist = watchlist;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public PharmacyDTO getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(PharmacyDTO pharmacy) {
        this.pharmacy = pharmacy;
    }

    public WatchlistItemDTO getWatchlistItem() {
        return watchlistItem;
    }

    public void setWatchlistItem(WatchlistItemDTO watchlistItem) {
        this.watchlistItem = watchlistItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDTO)) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", notificationType='" + getNotificationType() + "'" +
            ", message='" + getMessage() + "'" +
            ", emailSent='" + getEmailSent() + "'" +
            ", emailSentAt='" + getEmailSentAt() + "'" +
            ", emailDelivered='" + getEmailDelivered() + "'" +
            ", emailDeliveredAt='" + getEmailDeliveredAt() + "'" +
            ", emailFailed='" + getEmailFailed() + "'" +
            ", emailFailedAt='" + getEmailFailedAt() + "'" +
            ", emailFailureReason='" + getEmailFailureReason() + "'" +
            ", mailjetMessageId='" + getMailjetMessageId() + "'" +
            ", readAt='" + getReadAt() + "'" +
            ", watchlist=" + getWatchlist() +
            ", user=" + getUser() +
            ", pharmacy=" + getPharmacy() +
            ", watchlistItem=" + getWatchlistItem() +
            "}";
    }
}
