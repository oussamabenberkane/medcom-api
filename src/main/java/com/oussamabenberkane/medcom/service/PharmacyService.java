package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.oussamabenberkane.medcom.domain.Pharmacy}.
 */
public interface PharmacyService {
    /**
     * Save a pharmacy.
     *
     * @param pharmacyDTO the entity to save.
     * @return the persisted entity.
     */
    PharmacyDTO save(PharmacyDTO pharmacyDTO);

    /**
     * Updates a pharmacy.
     *
     * @param pharmacyDTO the entity to update.
     * @return the persisted entity.
     */
    PharmacyDTO update(PharmacyDTO pharmacyDTO);

    /**
     * Partially updates a pharmacy.
     *
     * @param pharmacyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PharmacyDTO> partialUpdate(PharmacyDTO pharmacyDTO);

    /**
     * Get all the PharmacyDTO where Watchlist is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<PharmacyDTO> findAllWhereWatchlistIsNull();

    /**
     * Get the "id" pharmacy.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PharmacyDTO> findOne(Long id);

    /**
     * Delete the "id" pharmacy.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
