package com.oussamabenberkane.medcom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oussamabenberkane.medcom.domain.enumeration.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Size(max = 1000)
    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "email_sent")
    private Boolean emailSent;

    @Column(name = "email_sent_at")
    private Instant emailSentAt;

    @Column(name = "email_delivered")
    private Boolean emailDelivered;

    @Column(name = "email_delivered_at")
    private Instant emailDeliveredAt;

    @Column(name = "email_failed")
    private Boolean emailFailed;

    @Column(name = "email_failed_at")
    private Instant emailFailedAt;

    @Size(max = 500)
    @Column(name = "email_failure_reason", length = 500)
    private String emailFailureReason;

    @Size(max = 255)
    @Column(name = "mailjet_message_id", length = 255)
    private String mailjetMessageId;

    @Column(name = "read_at")
    private Instant readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "pharmacy" }, allowSetters = true)
    private Watchlist watchlist;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "watchlist" }, allowSetters = true)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "watchlist", "product" }, allowSetters = true)
    private WatchlistItem watchlistItem;

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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Notification createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public Notification notificationType(NotificationType notificationType) {
        this.setNotificationType(notificationType);
        return this;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return this.message;
    }

    public Notification message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getEmailSent() {
        return this.emailSent;
    }

    public Notification emailSent(Boolean emailSent) {
        this.setEmailSent(emailSent);
        return this;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public Instant getEmailSentAt() {
        return this.emailSentAt;
    }

    public Notification emailSentAt(Instant emailSentAt) {
        this.setEmailSentAt(emailSentAt);
        return this;
    }

    public void setEmailSentAt(Instant emailSentAt) {
        this.emailSentAt = emailSentAt;
    }

    public Boolean getEmailDelivered() {
        return this.emailDelivered;
    }

    public Notification emailDelivered(Boolean emailDelivered) {
        this.setEmailDelivered(emailDelivered);
        return this;
    }

    public void setEmailDelivered(Boolean emailDelivered) {
        this.emailDelivered = emailDelivered;
    }

    public Instant getEmailDeliveredAt() {
        return this.emailDeliveredAt;
    }

    public Notification emailDeliveredAt(Instant emailDeliveredAt) {
        this.setEmailDeliveredAt(emailDeliveredAt);
        return this;
    }

    public void setEmailDeliveredAt(Instant emailDeliveredAt) {
        this.emailDeliveredAt = emailDeliveredAt;
    }

    public Boolean getEmailFailed() {
        return this.emailFailed;
    }

    public Notification emailFailed(Boolean emailFailed) {
        this.setEmailFailed(emailFailed);
        return this;
    }

    public void setEmailFailed(Boolean emailFailed) {
        this.emailFailed = emailFailed;
    }

    public Instant getEmailFailedAt() {
        return this.emailFailedAt;
    }

    public Notification emailFailedAt(Instant emailFailedAt) {
        this.setEmailFailedAt(emailFailedAt);
        return this;
    }

    public void setEmailFailedAt(Instant emailFailedAt) {
        this.emailFailedAt = emailFailedAt;
    }

    public String getEmailFailureReason() {
        return this.emailFailureReason;
    }

    public Notification emailFailureReason(String emailFailureReason) {
        this.setEmailFailureReason(emailFailureReason);
        return this;
    }

    public void setEmailFailureReason(String emailFailureReason) {
        this.emailFailureReason = emailFailureReason;
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

    public Instant getReadAt() {
        return this.readAt;
    }

    public Notification readAt(Instant readAt) {
        this.setReadAt(readAt);
        return this;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public Watchlist getWatchlist() {
        return this.watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Notification watchlist(Watchlist watchlist) {
        this.setWatchlist(watchlist);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification user(User user) {
        this.setUser(user);
        return this;
    }

    public Pharmacy getPharmacy() {
        return this.pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Notification pharmacy(Pharmacy pharmacy) {
        this.setPharmacy(pharmacy);
        return this;
    }

    public WatchlistItem getWatchlistItem() {
        return this.watchlistItem;
    }

    public void setWatchlistItem(WatchlistItem watchlistItem) {
        this.watchlistItem = watchlistItem;
    }

    public Notification watchlistItem(WatchlistItem watchlistItem) {
        this.setWatchlistItem(watchlistItem);
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
            "}";
    }
}
