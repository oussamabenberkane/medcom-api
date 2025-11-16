package com.oussamabenberkane.medcom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date_added", nullable = false)
    private Instant dateAdded;

    @Size(max = 50)
    @Column(name = "added_by", length = 50)
    private String addedBy;

    @Column(name = "date_updated")
    private Instant dateUpdated;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "last_availability_status")
    private Boolean lastAvailabilityStatus;

    @Column(name = "last_availability_change")
    private ZonedDateTime lastAvailabilityChange;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "pharmacy" }, allowSetters = true)
    private Watchlist watchlist;

    @ManyToOne(optional = false)
    @NotNull
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

    public Instant getDateAdded() {
        return this.dateAdded;
    }

    public WatchlistItem dateAdded(Instant dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
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

    public Instant getDateUpdated() {
        return this.dateUpdated;
    }

    public WatchlistItem dateUpdated(Instant dateUpdated) {
        this.setDateUpdated(dateUpdated);
        return this;
    }

    public void setDateUpdated(Instant dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public WatchlistItem updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public ZonedDateTime getLastAvailabilityChange() {
        return this.lastAvailabilityChange;
    }

    public WatchlistItem lastAvailabilityChange(ZonedDateTime lastAvailabilityChange) {
        this.setLastAvailabilityChange(lastAvailabilityChange);
        return this;
    }

    public void setLastAvailabilityChange(ZonedDateTime lastAvailabilityChange) {
        this.lastAvailabilityChange = lastAvailabilityChange;
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

    // prettier-ignore
    @Override
    public String toString() {
        return "WatchlistItem{" +
            "id=" + getId() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", addedBy='" + getAddedBy() + "'" +
            ", dateUpdated='" + getDateUpdated() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", lastAvailabilityStatus='" + getLastAvailabilityStatus() + "'" +
            ", lastAvailabilityChange='" + getLastAvailabilityChange() + "'" +
            "}";
    }
}
