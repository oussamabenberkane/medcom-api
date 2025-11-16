package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.oussamabenberkane.medcom.domain.WatchlistItem}.
 */
public interface WatchlistItemService {
    /**
     * Save a watchlistItem.
     *
     * @param watchlistItemDTO the entity to save.
     * @return the persisted entity.
     */
    WatchlistItemDTO save(WatchlistItemDTO watchlistItemDTO);

    /**
     * Updates a watchlistItem.
     *
     * @param watchlistItemDTO the entity to update.
     * @return the persisted entity.
     */
    WatchlistItemDTO update(WatchlistItemDTO watchlistItemDTO);

    /**
     * Partially updates a watchlistItem.
     *
     * @param watchlistItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WatchlistItemDTO> partialUpdate(WatchlistItemDTO watchlistItemDTO);

    /**
     * Get the "id" watchlistItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WatchlistItemDTO> findOne(Long id);

    /**
     * Delete the "id" watchlistItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
