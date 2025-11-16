package com.oussamabenberkane.medcom.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.oussamabenberkane.medcom.domain.WatchlistItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WatchlistItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant dateAdded;

    @Size(max = 50)
    private String addedBy;

    private Instant dateUpdated;

    @Size(max = 50)
    private String updatedBy;

    private Boolean lastAvailabilityStatus;

    private ZonedDateTime lastAvailabilityChange;

    @NotNull
    private WatchlistDTO watchlist;

    @NotNull
    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Instant getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Instant dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getLastAvailabilityStatus() {
        return lastAvailabilityStatus;
    }

    public void setLastAvailabilityStatus(Boolean lastAvailabilityStatus) {
        this.lastAvailabilityStatus = lastAvailabilityStatus;
    }

    public ZonedDateTime getLastAvailabilityChange() {
        return lastAvailabilityChange;
    }

    public void setLastAvailabilityChange(ZonedDateTime lastAvailabilityChange) {
        this.lastAvailabilityChange = lastAvailabilityChange;
    }

    public WatchlistDTO getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(WatchlistDTO watchlist) {
        this.watchlist = watchlist;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WatchlistItemDTO)) {
            return false;
        }

        WatchlistItemDTO watchlistItemDTO = (WatchlistItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, watchlistItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WatchlistItemDTO{" +
            "id=" + getId() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", addedBy='" + getAddedBy() + "'" +
            ", dateUpdated='" + getDateUpdated() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", lastAvailabilityStatus='" + getLastAvailabilityStatus() + "'" +
            ", lastAvailabilityChange='" + getLastAvailabilityChange() + "'" +
            ", watchlist=" + getWatchlist() +
            ", product=" + getProduct() +
            "}";
    }
}
