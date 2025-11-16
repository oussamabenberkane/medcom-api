package com.oussamabenberkane.medcom.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.oussamabenberkane.medcom.domain.WatchlistItem} entity. This class is used
 * in {@link com.oussamabenberkane.medcom.web.rest.WatchlistItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /watchlist-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WatchlistItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter dateAdded;

    private StringFilter addedBy;

    private InstantFilter dateUpdated;

    private StringFilter updatedBy;

    private BooleanFilter lastAvailabilityStatus;

    private ZonedDateTimeFilter lastAvailabilityChange;

    private LongFilter watchlistId;

    private LongFilter productId;

    private Boolean distinct;

    public WatchlistItemCriteria() {}

    public WatchlistItemCriteria(WatchlistItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.dateAdded = other.optionalDateAdded().map(InstantFilter::copy).orElse(null);
        this.addedBy = other.optionalAddedBy().map(StringFilter::copy).orElse(null);
        this.dateUpdated = other.optionalDateUpdated().map(InstantFilter::copy).orElse(null);
        this.updatedBy = other.optionalUpdatedBy().map(StringFilter::copy).orElse(null);
        this.lastAvailabilityStatus = other.optionalLastAvailabilityStatus().map(BooleanFilter::copy).orElse(null);
        this.lastAvailabilityChange = other.optionalLastAvailabilityChange().map(ZonedDateTimeFilter::copy).orElse(null);
        this.watchlistId = other.optionalWatchlistId().map(LongFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WatchlistItemCriteria copy() {
        return new WatchlistItemCriteria(this);
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

    public InstantFilter getDateAdded() {
        return dateAdded;
    }

    public Optional<InstantFilter> optionalDateAdded() {
        return Optional.ofNullable(dateAdded);
    }

    public InstantFilter dateAdded() {
        if (dateAdded == null) {
            setDateAdded(new InstantFilter());
        }
        return dateAdded;
    }

    public void setDateAdded(InstantFilter dateAdded) {
        this.dateAdded = dateAdded;
    }

    public StringFilter getAddedBy() {
        return addedBy;
    }

    public Optional<StringFilter> optionalAddedBy() {
        return Optional.ofNullable(addedBy);
    }

    public StringFilter addedBy() {
        if (addedBy == null) {
            setAddedBy(new StringFilter());
        }
        return addedBy;
    }

    public void setAddedBy(StringFilter addedBy) {
        this.addedBy = addedBy;
    }

    public InstantFilter getDateUpdated() {
        return dateUpdated;
    }

    public Optional<InstantFilter> optionalDateUpdated() {
        return Optional.ofNullable(dateUpdated);
    }

    public InstantFilter dateUpdated() {
        if (dateUpdated == null) {
            setDateUpdated(new InstantFilter());
        }
        return dateUpdated;
    }

    public void setDateUpdated(InstantFilter dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public StringFilter getUpdatedBy() {
        return updatedBy;
    }

    public Optional<StringFilter> optionalUpdatedBy() {
        return Optional.ofNullable(updatedBy);
    }

    public StringFilter updatedBy() {
        if (updatedBy == null) {
            setUpdatedBy(new StringFilter());
        }
        return updatedBy;
    }

    public void setUpdatedBy(StringFilter updatedBy) {
        this.updatedBy = updatedBy;
    }

    public BooleanFilter getLastAvailabilityStatus() {
        return lastAvailabilityStatus;
    }

    public Optional<BooleanFilter> optionalLastAvailabilityStatus() {
        return Optional.ofNullable(lastAvailabilityStatus);
    }

    public BooleanFilter lastAvailabilityStatus() {
        if (lastAvailabilityStatus == null) {
            setLastAvailabilityStatus(new BooleanFilter());
        }
        return lastAvailabilityStatus;
    }

    public void setLastAvailabilityStatus(BooleanFilter lastAvailabilityStatus) {
        this.lastAvailabilityStatus = lastAvailabilityStatus;
    }

    public ZonedDateTimeFilter getLastAvailabilityChange() {
        return lastAvailabilityChange;
    }

    public Optional<ZonedDateTimeFilter> optionalLastAvailabilityChange() {
        return Optional.ofNullable(lastAvailabilityChange);
    }

    public ZonedDateTimeFilter lastAvailabilityChange() {
        if (lastAvailabilityChange == null) {
            setLastAvailabilityChange(new ZonedDateTimeFilter());
        }
        return lastAvailabilityChange;
    }

    public void setLastAvailabilityChange(ZonedDateTimeFilter lastAvailabilityChange) {
        this.lastAvailabilityChange = lastAvailabilityChange;
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

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
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
        final WatchlistItemCriteria that = (WatchlistItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(dateAdded, that.dateAdded) &&
            Objects.equals(addedBy, that.addedBy) &&
            Objects.equals(dateUpdated, that.dateUpdated) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(lastAvailabilityStatus, that.lastAvailabilityStatus) &&
            Objects.equals(lastAvailabilityChange, that.lastAvailabilityChange) &&
            Objects.equals(watchlistId, that.watchlistId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            dateAdded,
            addedBy,
            dateUpdated,
            updatedBy,
            lastAvailabilityStatus,
            lastAvailabilityChange,
            watchlistId,
            productId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WatchlistItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDateAdded().map(f -> "dateAdded=" + f + ", ").orElse("") +
            optionalAddedBy().map(f -> "addedBy=" + f + ", ").orElse("") +
            optionalDateUpdated().map(f -> "dateUpdated=" + f + ", ").orElse("") +
            optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
            optionalLastAvailabilityStatus().map(f -> "lastAvailabilityStatus=" + f + ", ").orElse("") +
            optionalLastAvailabilityChange().map(f -> "lastAvailabilityChange=" + f + ", ").orElse("") +
            optionalWatchlistId().map(f -> "watchlistId=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
