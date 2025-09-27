package com.pharmaresolve.medcom.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.pharmaresolve.medcom.domain.WatchlistItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WatchlistItemDTO implements Serializable {

    private Long id;

    private ZonedDateTime dateAdded;

    private ZonedDateTime dateUpdated;

    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 3, message = "Priority must be at most 3")
    private Integer priority;

    private String addedBy;

    private String updatedBy;

    private Boolean alertEnabled;

    private Boolean lastAvailabilityStatus;

    private ZonedDateTime lastAvailabilityCheck;

    private Long watchlistId;

    private Long productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(ZonedDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public ZonedDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(ZonedDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAddedBy() {
        return addedBy;
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
        return alertEnabled;
    }

    public void setAlertEnabled(Boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Boolean getLastAvailabilityStatus() {
        return lastAvailabilityStatus;
    }

    public void setLastAvailabilityStatus(Boolean lastAvailabilityStatus) {
        this.lastAvailabilityStatus = lastAvailabilityStatus;
    }

    public ZonedDateTime getLastAvailabilityCheck() {
        return lastAvailabilityCheck;
    }

    public void setLastAvailabilityCheck(ZonedDateTime lastAvailabilityCheck) {
        this.lastAvailabilityCheck = lastAvailabilityCheck;
    }

    public Long getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(Long watchlistId) {
        this.watchlistId = watchlistId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WatchlistItemDTO watchlistItemDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, watchlistItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "WatchlistItemDTO{" +
            "id=" + getId() +
            ", dateAdded=" + getDateAdded() +
            ", dateUpdated=" + getDateUpdated() +
            ", priority=" + getPriority() +
            ", addedBy='" + getAddedBy() + '\'' +
            ", updatedBy='" + getUpdatedBy() + '\'' +
            ", alertEnabled=" + getAlertEnabled() +
            ", lastAvailabilityStatus=" + getLastAvailabilityStatus() +
            ", lastAvailabilityCheck=" + getLastAvailabilityCheck() +
            ", watchlistId=" + getWatchlistId() +
            ", productId=" + getProductId() +
            '}';
    }
}
