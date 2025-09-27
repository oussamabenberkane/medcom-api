package com.pharmaresolve.medcom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WatchlistItem.
 */
@Entity
@Table(name = "watchlist_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WatchlistItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "watchlist_item_sequence")
    @SequenceGenerator(name = "watchlist_item_sequence", sequenceName = "watchlist_item_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_added")
    private ZonedDateTime dateAdded;

    @Column(name = "date_updated")
    private ZonedDateTime dateUpdated;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "alert_enabled")
    private Boolean alertEnabled;

    @Column(name = "last_availability_status")
    private Boolean lastAvailabilityStatus;

    @Column(name = "last_availability_check")
    private ZonedDateTime lastAvailabilityCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "pharmacy" }, allowSetters = true)
    private Watchlist watchlist;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WatchlistItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateAdded() {
        return this.dateAdded;
    }

    public WatchlistItem dateAdded(ZonedDateTime dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public ZonedDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(ZonedDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setDateAdded(ZonedDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public WatchlistItem priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAddedBy() {
        return this.addedBy;
    }

    public WatchlistItem addedBy(String addedBy) {
        this.setAddedBy(addedBy);
        return this;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getAlertEnabled() {
        return this.alertEnabled;
    }

    public WatchlistItem alertEnabled(Boolean alertEnabled) {
        this.setAlertEnabled(alertEnabled);
        return this;
    }

    public void setAlertEnabled(Boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Boolean getLastAvailabilityStatus() {
        return this.lastAvailabilityStatus;
    }

    public WatchlistItem lastAvailabilityStatus(Boolean lastAvailabilityStatus) {
        this.setLastAvailabilityStatus(lastAvailabilityStatus);
        return this;
    }

    public void setLastAvailabilityStatus(Boolean lastAvailabilityStatus) {
        this.lastAvailabilityStatus = lastAvailabilityStatus;
    }

    public ZonedDateTime getLastAvailabilityCheck() {
        return this.lastAvailabilityCheck;
    }

    public WatchlistItem lastAvailabilityCheck(ZonedDateTime lastAvailabilityCheck) {
        this.setLastAvailabilityCheck(lastAvailabilityCheck);
        return this;
    }

    public void setLastAvailabilityCheck(ZonedDateTime lastAvailabilityCheck) {
        this.lastAvailabilityCheck = lastAvailabilityCheck;
    }

    public Watchlist getWatchlist() {
        return this.watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public WatchlistItem watchlist(Watchlist watchlist) {
        this.setWatchlist(watchlist);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public WatchlistItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WatchlistItem)) {
            return false;
        }
        return getId() != null && getId().equals(((WatchlistItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "WatchlistItem{" +
            "id=" + getId() +
            ", dateAdded=" + getDateAdded() +
            ", dateUpdated=" + getDateUpdated() +
            ", priority=" + getPriority() +
            ", addedBy='" + getAddedBy() + '\'' +
            ", updatedBy='" + getUpdatedBy() + '\'' +
            ", alertEnabled=" + getAlertEnabled() +
            ", lastAvailabilityStatus=" + getLastAvailabilityStatus() +
            ", lastAvailabilityCheck=" + getLastAvailabilityCheck() +
            ", watchlist=" + getWatchlist() +
            ", product=" + getProduct() +
            '}';
    }
}
