package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.oussamabenberkane.medcom.domain.Watchlist}.
 */
public interface WatchlistService {
    /**
     * Save a watchlist.
     *
     * @param watchlistDTO the entity to save.
     * @return the persisted entity.
     */
    WatchlistDTO save(WatchlistDTO watchlistDTO);

    /**
     * Updates a watchlist.
     *
     * @param watchlistDTO the entity to update.
     * @return the persisted entity.
     */
    WatchlistDTO update(WatchlistDTO watchlistDTO);

    /**
     * Partially updates a watchlist.
     *
     * @param watchlistDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WatchlistDTO> partialUpdate(WatchlistDTO watchlistDTO);

    /**
     * Get all the watchlists.
     *
     * @return the list of entities.
     */
    List<WatchlistDTO> findAll();

    /**
     * Get the "id" watchlist.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WatchlistDTO> findOne(Long id);

    /**
     * Delete the "id" watchlist.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
