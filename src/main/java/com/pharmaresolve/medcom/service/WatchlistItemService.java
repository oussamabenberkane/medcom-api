package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.WatchlistItem;
import com.pharmaresolve.medcom.repository.WatchlistItemRepository;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import com.pharmaresolve.medcom.service.dto.WatchlistItemDTO;
import com.pharmaresolve.medcom.service.mapper.WatchlistItemMapper;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
import com.pharmaresolve.medcom.web.rest.errors.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.pharmaresolve.medcom.domain.WatchlistItem}.
 */
@Service
@Transactional
public class WatchlistItemService {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistItemService.class);

    private final WatchlistItemRepository watchlistItemRepository;
    private final WatchlistItemMapper watchlistItemMapper;
    private final WatchlistService watchlistService;
    private final ProductService productService;

    public WatchlistItemService(WatchlistItemRepository watchlistItemRepository, WatchlistItemMapper watchlistItemMapper,
                                WatchlistService watchlistService, ProductService productService) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.watchlistItemMapper = watchlistItemMapper;
        this.watchlistService = watchlistService;
        this.productService = productService;
    }

    /**
     * Save a watchlist item.
     */
    public WatchlistItemDTO save(WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to save WatchlistItem : {}", watchlistItemDTO);
        WatchlistItem watchlistItem = watchlistItemMapper.toEntity(watchlistItemDTO);
        watchlistItem = watchlistItemRepository.save(watchlistItem);
        return watchlistItemMapper.toDto(watchlistItem);
    }

    /**
     * Add an item to a pharmacy's watchlist.
     */
    public WatchlistItemDTO addItemToWatchlist(Long pharmacyId, WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to add WatchlistItem to pharmacy {} watchlist : {}", pharmacyId, watchlistItemDTO);

        WatchlistDTO watchlist = watchlistService.findOne(pharmacyId)
            .orElseThrow(() -> new BadRequestAlertException("Watchlist not found for pharmacy", "watchlist", "watchlistnotfound"));

        if (watchlistItemDTO.getProductId() != null) {
            productService.findOne(watchlistItemDTO.getProductId())
                .orElseThrow(() -> new BadRequestAlertException("Product not found", "product", "productnotfound"));

            if (watchlistItemRepository.existsByWatchlistIdAndProductId(pharmacyId, watchlistItemDTO.getProductId())) {
                throw new BadRequestAlertException("Product already exists in this watchlist", "watchlistItem", "productduplicate");
            }
        } else {
            throw new BadRequestAlertException("Product is required", "watchlistItem", "productrequired");
        }

        long currentItemCount = watchlistItemRepository.countByWatchlistId(pharmacyId);
        if (currentItemCount >= watchlist.getLimit()) {
            throw new BadRequestAlertException("Watchlist limit exceeded", "watchlistItem", ErrorConstants.WATCHLIST_LIMIT_EXCEEDED);
        }

        // Set default values and associations
        watchlistItemDTO.setWatchlistId(watchlist.getId());
        watchlistItemDTO.setDateAdded(ZonedDateTime.now());
        watchlistItemDTO.setAddedBy("Admin");
        watchlistItemDTO.setAlertEnabled(true);
        if (watchlistItemDTO.getPriority() == null) {
            watchlistItemDTO.setPriority(1);
        } else if (watchlistItemDTO.getPriority() <= 0){
            throw new BadRequestAlertException("Please provide a strictly positive priority", "watchlistItem", "priorityinvalid");
        }

        return save(watchlistItemDTO);
    }

    /**
     * Update an item in a pharmacy's watchlist.
     */
    public WatchlistItemDTO updateItemInWatchlist(Long pharmacyId, Long itemId, WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to update WatchlistItem {} in pharmacy {} watchlist : {}", itemId, pharmacyId, watchlistItemDTO);

        WatchlistItemDTO existingItem = findItemByIdAndPharmacy(itemId, pharmacyId)
            .orElseThrow(() -> new BadRequestAlertException("Watchlist item not found or doesn't belong to this pharmacy", "watchlistItem", "itemnotfound"));

        // Set audit fields for update
        if (watchlistItemDTO.getPriority() <= 0){
            throw new BadRequestAlertException("Please provide a strictly positive priority", "watchlistItem", "priorityinvalid");
        }
        existingItem.setPriority(watchlistItemDTO.getPriority());
        if (watchlistItemDTO.getAlertEnabled() != null) existingItem.setAlertEnabled(watchlistItemDTO.getAlertEnabled());
        existingItem.setDateUpdated(ZonedDateTime.now());
        existingItem.setUpdatedBy("Admin");

        return save(existingItem);
    }

    /**
     * Remove an item from a pharmacy's watchlist.
     */
    public void removeItemFromWatchlist(Long pharmacyId, Long itemId) {
        LOG.debug("Request to remove WatchlistItem {} from pharmacy {} watchlist", itemId, pharmacyId);

        boolean exists = findItemByIdAndPharmacy(itemId, pharmacyId).isPresent();
        if (!exists) {
            throw new BadRequestAlertException("Watchlist item not found or doesn't belong to this pharmacy", "watchlistItem", "itemnotfound");
        }

        watchlistItemRepository.deleteById(itemId);
    }

    /**
     * Get all watchlist items for a pharmacy (paged).
     */
    @Transactional(readOnly = true)
    public Page<WatchlistItemDTO> findItemsByWatchlist(Long pharmacyId, Pageable pageable) {
        LOG.debug("Request to get all WatchlistItems for pharmacy {}", pharmacyId);
        return watchlistItemRepository.findByWatchlistId(pharmacyId, pageable).map(watchlistItemMapper::toDto);
    }

    /**
     * Get a watchlist item by ID for a pharmacy.
     */
    @Transactional(readOnly = true)
    public Optional<WatchlistItemDTO> findItemByIdAndPharmacy(Long itemId, Long pharmacyId) {
        LOG.debug("Request to get WatchlistItem {} for pharmacy {}", itemId, pharmacyId);
        return watchlistItemRepository.findByIdAndWatchlistId(itemId, pharmacyId).map(watchlistItemMapper::toDto);
    }

    /**
     * Get all watchlist items (paged).
     */
    @Transactional(readOnly = true)
    public Page<WatchlistItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all WatchlistItems");
        return watchlistItemRepository.findAll(pageable).map(watchlistItemMapper::toDto);
    }

    /**
     * Get a watchlist item by ID.
     */
    @Transactional(readOnly = true)
    public Optional<WatchlistItemDTO> findOne(Long id) {
        LOG.debug("Request to get WatchlistItem : {}", id);
        return watchlistItemRepository.findById(id).map(watchlistItemMapper::toDto);
    }

    /**
     * Delete a watchlist item by ID.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WatchlistItem : {}", id);
        watchlistItemRepository.deleteById(id);
    }
}
