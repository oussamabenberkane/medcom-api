package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Watchlist;
import com.pharmaresolve.medcom.repository.PharmacyRepository;
import com.pharmaresolve.medcom.repository.WatchlistRepository;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import com.pharmaresolve.medcom.service.mapper.WatchlistMapper;

import java.util.Optional;

import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.pharmaresolve.medcom.domain.Watchlist}.
 */
@Service
@Transactional
public class WatchlistService {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;
    private final PharmacyRepository pharmacyRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, WatchlistMapper watchlistMapper, PharmacyRepository pharmacyRepository) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistMapper = watchlistMapper;
        this.pharmacyRepository = pharmacyRepository;
    }

    /**
     * Save a watchlist.
     */
    public WatchlistDTO save(WatchlistDTO watchlistDTO) {
        LOG.debug("Request to save Watchlist : {}", watchlistDTO);

        Watchlist watchlist = watchlistMapper.toEntity(watchlistDTO);
        if (watchlist.getPharmacy() != null && watchlist.getPharmacy().getId() != null) {
            Long pharmacyId = watchlist.getPharmacy().getId();
            pharmacyRepository.findById(pharmacyId).ifPresent(watchlist::setPharmacy);
        }

        watchlist = watchlistRepository.save(watchlist);
        return watchlistMapper.toDto(watchlist);
    }

    /**
     * Update the watchlist of a pharmacy.
     */
    public WatchlistDTO update(Long pharmacyId, WatchlistDTO watchlistDTO) {
        LOG.debug("Request to update Watchlist for pharmacy : {}", pharmacyId);

        WatchlistDTO existingWatchlist = findOne(pharmacyId)
            .orElseThrow(() -> new BadRequestAlertException("Watchlist not found for pharmacy", "watchlist", "notfound"));

        if (watchlistDTO.getName() != null && !watchlistDTO.getName().trim().isEmpty()) {
            existingWatchlist.setName(watchlistDTO.getName());
        }

        if (watchlistDTO.getLimit() != null && watchlistDTO.getLimit() > 0) {
            existingWatchlist.setLimit(watchlistDTO.getLimit());
        }

        return save(existingWatchlist);
    }

    /**
     * Get all watchlists (paged).
     */
    @Transactional(readOnly = true)
    public Page<WatchlistDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Watchlists");
        return watchlistRepository.findAll(pageable).map(watchlistMapper::toDto);
    }

    /**
     * Get a watchlist by ID.
     */
    @Transactional(readOnly = true)
    public Optional<WatchlistDTO> findOne(Long id) {
        LOG.debug("Request to get Watchlist : {}", id);
        return watchlistRepository.findById(id).map(watchlistMapper::toDto);
    }

    /**
     * Delete a watchlist by ID.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Watchlist : {}", id);
        watchlistRepository.deleteById(id);
    }
}
