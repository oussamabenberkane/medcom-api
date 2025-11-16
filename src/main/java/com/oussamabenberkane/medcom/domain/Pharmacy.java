package com.oussamabenberkane.medcom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Pharmacy.
 */
@Entity
@Table(name = "pharmacy")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pharmacy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 255)
    @Column(name = "website", length = 255)
    private String website;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Size(max = 50)
    @Column(name = "activated_by", length = 50)
    private String activatedBy;

    @Size(max = 50)
    @Column(name = "deactivated_by", length = 50)
    private String deactivatedBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Size(max = 50)
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    @JsonIgnoreProperties(value = { "pharmacy" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "pharmacy")
    private Watchlist watchlist;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pharmacy id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Pharmacy name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public Pharmacy address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public Pharmacy email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Pharmacy phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return this.website;
    }

    public Pharmacy website(String website) {
        this.setWebsite(website);
        return this;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Pharmacy active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getActivatedBy() {
        return this.activatedBy;
    }

    public Pharmacy activatedBy(String activatedBy) {
        this.setActivatedBy(activatedBy);
        return this;
    }

    public void setActivatedBy(String activatedBy) {
        this.activatedBy = activatedBy;
    }

    public String getDeactivatedBy() {
        return this.deactivatedBy;
    }

    public Pharmacy deactivatedBy(String deactivatedBy) {
        this.setDeactivatedBy(deactivatedBy);
        return this;
    }

    public void setDeactivatedBy(String deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Pharmacy deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public Pharmacy deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Watchlist getWatchlist() {
        return this.watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        if (this.watchlist != null) {
            this.watchlist.setPharmacy(null);
        }
        if (watchlist != null) {
            watchlist.setPharmacy(this);
        }
        this.watchlist = watchlist;
    }

    public Pharmacy watchlist(Watchlist watchlist) {
        this.setWatchlist(watchlist);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pharmacy)) {
            return false;
        }
        return getId() != null && getId().equals(((Pharmacy) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pharmacy{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", website='" + getWebsite() + "'" +
            ", active='" + getActive() + "'" +
            ", activatedBy='" + getActivatedBy() + "'" +
            ", deactivatedBy='" + getDeactivatedBy() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
