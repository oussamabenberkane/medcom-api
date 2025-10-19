package com.pharmaresolve.medcom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmaresolve.medcom.domain.enumeration.NotificationType;
import com.pharmaresolve.medcom.domain.enumeration.NotificationStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_sequence")
    @SequenceGenerator(name = "notification_sequence", sequenceName = "notification_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Column(name = "content")
    private String content;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "delivered_at")
    private ZonedDateTime deliveredAt;

    @Column(name = "mailjet_message_id")
    private String mailjetMessageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "watchlistItem" }, allowSetters = true)
    private Alert alert;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return this.type;
    }

    public Notification type(NotificationType type) {
        this.setType(type);
        return this;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public Notification content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getSentAt() {
        return this.sentAt;
    }

    public Notification sentAt(ZonedDateTime sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public Notification deliveredAt(ZonedDateTime deliveredAt) {
        this.setDeliveredAt(deliveredAt);
        return this;
    }

    public void setDeliveredAt(ZonedDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getMailjetMessageId() {
        return this.mailjetMessageId;
    }

    public Notification mailjetMessageId(String mailjetMessageId) {
        this.setMailjetMessageId(mailjetMessageId);
        return this;
    }

    public void setMailjetMessageId(String mailjetMessageId) {
        this.mailjetMessageId = mailjetMessageId;
    }

    public NotificationStatus getStatus() {
        return this.status;
    }

    public Notification status(NotificationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Notification errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRecipientEmail() {
        return this.recipientEmail;
    }

    public Notification recipientEmail(String recipientEmail) {
        this.setRecipientEmail(recipientEmail);
        return this;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientName() {
        return this.recipientName;
    }

    public Notification recipientName(String recipientName) {
        this.setRecipientName(recipientName);
        return this;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public ZonedDateTime getReadAt() {
        return this.readAt;
    }

    public Notification readAt(ZonedDateTime readAt) {
        this.setReadAt(readAt);
        return this;
    }

    public void setReadAt(ZonedDateTime readAt) {
        this.readAt = readAt;
    }

    public Alert getAlert() {
        return this.alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Notification alert(Alert alert) {
        this.setAlert(alert);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return getId() != null && getId().equals(((Notification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", content='" + getContent() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            ", mailjetMessageId='" + getMailjetMessageId() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", recipientEmail='" + getRecipientEmail() + "'" +
            ", recipientName='" + getRecipientName() + "'" +
            ", readAt='" + getReadAt() + "'" +
            "}";
    }
}
