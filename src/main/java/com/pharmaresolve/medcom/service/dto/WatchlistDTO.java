package com.pharmaresolve.medcom.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.pharmaresolve.medcom.domain.Watchlist} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WatchlistDTO implements Serializable {

    private Long id;

    private String name;

    private Integer limit;

    private Long pharmacyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WatchlistDTO watchlistDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, watchlistDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "WatchlistDTO{" +
            "id=" + getId() +
            ", name='" + getName() + '\'' +
            ", limit=" + getLimit() +
            ", pharmacyId=" + getPharmacyId() +
            '}';
    }
}
