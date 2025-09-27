package com.pharmaresolve.medcom.service.dto;

import com.pharmaresolve.medcom.domain.enumeration.AlertStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.pharmaresolve.medcom.domain.Alert} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertDTO implements Serializable {

    private Long id;

    private AlertStatus status;

    private String message;

    private ZonedDateTime created;

    private ZonedDateTime sentAt;

    private ZonedDateTime resolvedAt;

    private String mailjetMessageId;

    private WatchlistItemDTO watchlistItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getMailjetMessageId() {
        return mailjetMessageId;
    }

    public void setMailjetMessageId(String mailjetMessageId) {
        this.mailjetMessageId = mailjetMessageId;
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
        if (!(o instanceof AlertDTO)) {
            return false;
        }

        AlertDTO alertDTO = (AlertDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, alertDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", created='" + getCreated() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", mailjetMessageId='" + getMailjetMessageId() + "'" +
            ", watchlistItem=" + getWatchlistItem() +
            "}";
    }
}
