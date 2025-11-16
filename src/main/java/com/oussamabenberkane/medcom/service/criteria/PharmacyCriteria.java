package com.oussamabenberkane.medcom.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.oussamabenberkane.medcom.domain.Pharmacy} entity. This class is used
 * in {@link com.oussamabenberkane.medcom.web.rest.PharmacyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pharmacies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PharmacyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter address;

    private StringFilter email;

    private StringFilter phone;

    private StringFilter website;

    private BooleanFilter active;

    private StringFilter activatedBy;

    private StringFilter deactivatedBy;

    private BooleanFilter deleted;

    private StringFilter deletedBy;

    private LongFilter watchlistId;

    private Boolean distinct;

    public PharmacyCriteria() {}

    public PharmacyCriteria(PharmacyCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.website = other.optionalWebsite().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.activatedBy = other.optionalActivatedBy().map(StringFilter::copy).orElse(null);
        this.deactivatedBy = other.optionalDeactivatedBy().map(StringFilter::copy).orElse(null);
        this.deleted = other.optionalDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(StringFilter::copy).orElse(null);
        this.watchlistId = other.optionalWatchlistId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PharmacyCriteria copy() {
        return new PharmacyCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getWebsite() {
        return website;
    }

    public Optional<StringFilter> optionalWebsite() {
        return Optional.ofNullable(website);
    }

    public StringFilter website() {
        if (website == null) {
            setWebsite(new StringFilter());
        }
        return website;
    }

    public void setWebsite(StringFilter website) {
        this.website = website;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public StringFilter getActivatedBy() {
        return activatedBy;
    }

    public Optional<StringFilter> optionalActivatedBy() {
        return Optional.ofNullable(activatedBy);
    }

    public StringFilter activatedBy() {
        if (activatedBy == null) {
            setActivatedBy(new StringFilter());
        }
        return activatedBy;
    }

    public void setActivatedBy(StringFilter activatedBy) {
        this.activatedBy = activatedBy;
    }

    public StringFilter getDeactivatedBy() {
        return deactivatedBy;
    }

    public Optional<StringFilter> optionalDeactivatedBy() {
        return Optional.ofNullable(deactivatedBy);
    }

    public StringFilter deactivatedBy() {
        if (deactivatedBy == null) {
            setDeactivatedBy(new StringFilter());
        }
        return deactivatedBy;
    }

    public void setDeactivatedBy(StringFilter deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

    public BooleanFilter getDeleted() {
        return deleted;
    }

    public Optional<BooleanFilter> optionalDeleted() {
        return Optional.ofNullable(deleted);
    }

    public BooleanFilter deleted() {
        if (deleted == null) {
            setDeleted(new BooleanFilter());
        }
        return deleted;
    }

    public void setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
    }

    public StringFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<StringFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public StringFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new StringFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(StringFilter deletedBy) {
        this.deletedBy = deletedBy;
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
        final PharmacyCriteria that = (PharmacyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(address, that.address) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(website, that.website) &&
            Objects.equals(active, that.active) &&
            Objects.equals(activatedBy, that.activatedBy) &&
            Objects.equals(deactivatedBy, that.deactivatedBy) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(watchlistId, that.watchlistId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            address,
            email,
            phone,
            website,
            active,
            activatedBy,
            deactivatedBy,
            deleted,
            deletedBy,
            watchlistId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PharmacyCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalWebsite().map(f -> "website=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalActivatedBy().map(f -> "activatedBy=" + f + ", ").orElse("") +
            optionalDeactivatedBy().map(f -> "deactivatedBy=" + f + ", ").orElse("") +
            optionalDeleted().map(f -> "deleted=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalWatchlistId().map(f -> "watchlistId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
