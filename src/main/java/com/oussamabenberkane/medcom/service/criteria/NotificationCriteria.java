package com.oussamabenberkane.medcom.service.criteria;

import com.oussamabenberkane.medcom.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.oussamabenberkane.medcom.domain.Notification} entity. This class is used
 * in {@link com.oussamabenberkane.medcom.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering NotificationType
     */
    public static class NotificationTypeFilter extends Filter<NotificationType> {

        public NotificationTypeFilter() {}

        public NotificationTypeFilter(NotificationTypeFilter filter) {
            super(filter);
        }

        @Override
        public NotificationTypeFilter copy() {
            return new NotificationTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter createdAt;

    private NotificationTypeFilter notificationType;

    private StringFilter message;

    private BooleanFilter emailSent;

    private InstantFilter emailSentAt;

    private BooleanFilter emailDelivered;

    private InstantFilter emailDeliveredAt;

    private BooleanFilter emailFailed;

    private InstantFilter emailFailedAt;

    private StringFilter emailFailureReason;

    private StringFilter mailjetMessageId;

    private InstantFilter readAt;

    private LongFilter watchlistId;

    private LongFilter userId;

    private LongFilter pharmacyId;

    private LongFilter watchlistItemId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.notificationType = other.optionalNotificationType().map(NotificationTypeFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.emailSent = other.optionalEmailSent().map(BooleanFilter::copy).orElse(null);
        this.emailSentAt = other.optionalEmailSentAt().map(InstantFilter::copy).orElse(null);
        this.emailDelivered = other.optionalEmailDelivered().map(BooleanFilter::copy).orElse(null);
        this.emailDeliveredAt = other.optionalEmailDeliveredAt().map(InstantFilter::copy).orElse(null);
        this.emailFailed = other.optionalEmailFailed().map(BooleanFilter::copy).orElse(null);
        this.emailFailedAt = other.optionalEmailFailedAt().map(InstantFilter::copy).orElse(null);
        this.emailFailureReason = other.optionalEmailFailureReason().map(StringFilter::copy).orElse(null);
        this.mailjetMessageId = other.optionalMailjetMessageId().map(StringFilter::copy).orElse(null);
        this.readAt = other.optionalReadAt().map(InstantFilter::copy).orElse(null);
        this.watchlistId = other.optionalWatchlistId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.pharmacyId = other.optionalPharmacyId().map(LongFilter::copy).orElse(null);
        this.watchlistItemId = other.optionalWatchlistItemId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationTypeFilter getNotificationType() {
        return notificationType;
    }

    public Optional<NotificationTypeFilter> optionalNotificationType() {
        return Optional.ofNullable(notificationType);
    }

    public NotificationTypeFilter notificationType() {
        if (notificationType == null) {
            setNotificationType(new NotificationTypeFilter());
        }
        return notificationType;
    }

    public void setNotificationType(NotificationTypeFilter notificationType) {
        this.notificationType = notificationType;
    }

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public BooleanFilter getEmailSent() {
        return emailSent;
    }

    public Optional<BooleanFilter> optionalEmailSent() {
        return Optional.ofNullable(emailSent);
    }

    public BooleanFilter emailSent() {
        if (emailSent == null) {
            setEmailSent(new BooleanFilter());
        }
        return emailSent;
    }

    public void setEmailSent(BooleanFilter emailSent) {
        this.emailSent = emailSent;
    }

    public InstantFilter getEmailSentAt() {
        return emailSentAt;
    }

    public Optional<InstantFilter> optionalEmailSentAt() {
        return Optional.ofNullable(emailSentAt);
    }

    public InstantFilter emailSentAt() {
        if (emailSentAt == null) {
            setEmailSentAt(new InstantFilter());
        }
        return emailSentAt;
    }

    public void setEmailSentAt(InstantFilter emailSentAt) {
        this.emailSentAt = emailSentAt;
    }

    public BooleanFilter getEmailDelivered() {
        return emailDelivered;
    }

    public Optional<BooleanFilter> optionalEmailDelivered() {
        return Optional.ofNullable(emailDelivered);
    }

    public BooleanFilter emailDelivered() {
        if (emailDelivered == null) {
            setEmailDelivered(new BooleanFilter());
        }
        return emailDelivered;
    }

    public void setEmailDelivered(BooleanFilter emailDelivered) {
        this.emailDelivered = emailDelivered;
    }

    public InstantFilter getEmailDeliveredAt() {
        return emailDeliveredAt;
    }

    public Optional<InstantFilter> optionalEmailDeliveredAt() {
        return Optional.ofNullable(emailDeliveredAt);
    }

    public InstantFilter emailDeliveredAt() {
        if (emailDeliveredAt == null) {
            setEmailDeliveredAt(new InstantFilter());
        }
        return emailDeliveredAt;
    }

    public void setEmailDeliveredAt(InstantFilter emailDeliveredAt) {
        this.emailDeliveredAt = emailDeliveredAt;
    }

    public BooleanFilter getEmailFailed() {
        return emailFailed;
    }

    public Optional<BooleanFilter> optionalEmailFailed() {
        return Optional.ofNullable(emailFailed);
    }

    public BooleanFilter emailFailed() {
        if (emailFailed == null) {
            setEmailFailed(new BooleanFilter());
        }
        return emailFailed;
    }

    public void setEmailFailed(BooleanFilter emailFailed) {
        this.emailFailed = emailFailed;
    }

    public InstantFilter getEmailFailedAt() {
        return emailFailedAt;
    }

    public Optional<InstantFilter> optionalEmailFailedAt() {
        return Optional.ofNullable(emailFailedAt);
    }

    public InstantFilter emailFailedAt() {
        if (emailFailedAt == null) {
            setEmailFailedAt(new InstantFilter());
        }
        return emailFailedAt;
    }

    public void setEmailFailedAt(InstantFilter emailFailedAt) {
        this.emailFailedAt = emailFailedAt;
    }

    public StringFilter getEmailFailureReason() {
        return emailFailureReason;
    }

    public Optional<StringFilter> optionalEmailFailureReason() {
        return Optional.ofNullable(emailFailureReason);
    }

    public StringFilter emailFailureReason() {
        if (emailFailureReason == null) {
            setEmailFailureReason(new StringFilter());
        }
        return emailFailureReason;
    }

    public void setEmailFailureReason(StringFilter emailFailureReason) {
        this.emailFailureReason = emailFailureReason;
    }

    public StringFilter getMailjetMessageId() {
        return mailjetMessageId;
    }

    public Optional<StringFilter> optionalMailjetMessageId() {
        return Optional.ofNullable(mailjetMessageId);
    }

    public StringFilter mailjetMessageId() {
        if (mailjetMessageId == null) {
            setMailjetMessageId(new StringFilter());
        }
        return mailjetMessageId;
    }

    public void setMailjetMessageId(StringFilter mailjetMessageId) {
        this.mailjetMessageId = mailjetMessageId;
    }

    public InstantFilter getReadAt() {
        return readAt;
    }

    public Optional<InstantFilter> optionalReadAt() {
        return Optional.ofNullable(readAt);
    }

    public InstantFilter readAt() {
        if (readAt == null) {
            setReadAt(new InstantFilter());
        }
        return readAt;
    }

    public void setReadAt(InstantFilter readAt) {
        this.readAt = readAt;
    }

    public LongFilter getWatchlistId() {
        return watchlistId;
    }

    public Optional<LongFilter> optionalWatchlistId() {
        return Optional.ofNullable(watchlistId);
    }

    public LongFilter watchlistId() {
        if (watchlistId == null) {
            setWatchlistId(new LongFilter());
        }
        return watchlistId;
    }

    public void setWatchlistId(LongFilter watchlistId) {
        this.watchlistId = watchlistId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPharmacyId() {
        return pharmacyId;
    }

    public Optional<LongFilter> optionalPharmacyId() {
        return Optional.ofNullable(pharmacyId);
    }

    public LongFilter pharmacyId() {
        if (pharmacyId == null) {
            setPharmacyId(new LongFilter());
        }
        return pharmacyId;
    }

    public void setPharmacyId(LongFilter pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public LongFilter getWatchlistItemId() {
        return watchlistItemId;
    }

    public Optional<LongFilter> optionalWatchlistItemId() {
        return Optional.ofNullable(watchlistItemId);
    }

    public LongFilter watchlistItemId() {
        if (watchlistItemId == null) {
            setWatchlistItemId(new LongFilter());
        }
        return watchlistItemId;
    }

    public void setWatchlistItemId(LongFilter watchlistItemId) {
        this.watchlistItemId = watchlistItemId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(notificationType, that.notificationType) &&
            Objects.equals(message, that.message) &&
            Objects.equals(emailSent, that.emailSent) &&
            Objects.equals(emailSentAt, that.emailSentAt) &&
            Objects.equals(emailDelivered, that.emailDelivered) &&
            Objects.equals(emailDeliveredAt, that.emailDeliveredAt) &&
            Objects.equals(emailFailed, that.emailFailed) &&
            Objects.equals(emailFailedAt, that.emailFailedAt) &&
            Objects.equals(emailFailureReason, that.emailFailureReason) &&
            Objects.equals(mailjetMessageId, that.mailjetMessageId) &&
            Objects.equals(readAt, that.readAt) &&
            Objects.equals(watchlistId, that.watchlistId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(pharmacyId, that.pharmacyId) &&
            Objects.equals(watchlistItemId, that.watchlistItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            createdAt,
            notificationType,
            message,
            emailSent,
            emailSentAt,
            emailDelivered,
            emailDeliveredAt,
            emailFailed,
            emailFailedAt,
            emailFailureReason,
            mailjetMessageId,
            readAt,
            watchlistId,
            userId,
            pharmacyId,
            watchlistItemId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalNotificationType().map(f -> "notificationType=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalEmailSent().map(f -> "emailSent=" + f + ", ").orElse("") +
            optionalEmailSentAt().map(f -> "emailSentAt=" + f + ", ").orElse("") +
            optionalEmailDelivered().map(f -> "emailDelivered=" + f + ", ").orElse("") +
            optionalEmailDeliveredAt().map(f -> "emailDeliveredAt=" + f + ", ").orElse("") +
            optionalEmailFailed().map(f -> "emailFailed=" + f + ", ").orElse("") +
            optionalEmailFailedAt().map(f -> "emailFailedAt=" + f + ", ").orElse("") +
            optionalEmailFailureReason().map(f -> "emailFailureReason=" + f + ", ").orElse("") +
            optionalMailjetMessageId().map(f -> "mailjetMessageId=" + f + ", ").orElse("") +
            optionalReadAt().map(f -> "readAt=" + f + ", ").orElse("") +
            optionalWatchlistId().map(f -> "watchlistId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalPharmacyId().map(f -> "pharmacyId=" + f + ", ").orElse("") +
            optionalWatchlistItemId().map(f -> "watchlistItemId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
