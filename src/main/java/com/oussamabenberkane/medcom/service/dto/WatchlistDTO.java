package com.oussamabenberkane.medcom.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.oussamabenberkane.medcom.domain.Watchlist} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WatchlistDTO implements Serializable {

    private Long id;

    private Instant createdAt;

    @Size(max = 50)
    private String createdBy;

    @NotNull
    private PharmacyDTO pharmacy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public PharmacyDTO getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(PharmacyDTO pharmacy) {
        this.pharmacy = pharmacy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WatchlistDTO)) {
            return false;
        }

        WatchlistDTO watchlistDTO = (WatchlistDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, watchlistDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WatchlistDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", pharmacy=" + getPharmacy() +
            "}";
    }
}
