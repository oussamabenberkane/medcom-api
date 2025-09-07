package com.pharmaresolve.medcom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmaresolve.medcom.domain.enumeration.AlertStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Alert.
 */
@Entity
@Table(name = "alert")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_sequence")
    @SequenceGenerator(name = "alert_sequence", sequenceName = "alert_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlertStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "resolved_at")
    private ZonedDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "watchlist", "product" }, allowSetters = true)
    private WatchlistItem watchlistItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Alert id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlertStatus getStatus() {
        return this.status;
    }

    public Alert status(AlertStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public Alert message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Alert created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getSentAt() {
        return this.sentAt;
    }

    public Alert sentAt(ZonedDateTime sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getResolvedAt() {
        return this.resolvedAt;
    }

    public Alert resolvedAt(ZonedDateTime resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public WatchlistItem getWatchlistItem() {
        return this.watchlistItem;
    }

    public void setWatchlistItem(WatchlistItem watchlistItem) {
        this.watchlistItem = watchlistItem;
    }

    public Alert watchlistItem(WatchlistItem watchlistItem) {
        this.setWatchlistItem(watchlistItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alert)) {
            return false;
        }
        return getId() != null && getId().equals(((Alert) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Alert{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", created='" + getCreated() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            "}";
    }
}
