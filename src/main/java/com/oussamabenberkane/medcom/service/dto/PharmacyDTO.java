package com.oussamabenberkane.medcom.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.oussamabenberkane.medcom.domain.Pharmacy} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PharmacyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String address;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String website;

    @NotNull
    private Boolean active;

    @Size(max = 50)
    private String activatedBy;

    @Size(max = 50)
    private String deactivatedBy;

    private Boolean deleted;

    @Size(max = 50)
    private String deletedBy;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getActivatedBy() {
        return activatedBy;
    }

    public void setActivatedBy(String activatedBy) {
        this.activatedBy = activatedBy;
    }

    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    public void setDeactivatedBy(String deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PharmacyDTO)) {
            return false;
        }

        PharmacyDTO pharmacyDTO = (PharmacyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pharmacyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PharmacyDTO{" +
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
