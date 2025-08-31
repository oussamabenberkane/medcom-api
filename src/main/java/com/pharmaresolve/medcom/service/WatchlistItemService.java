package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.WatchlistItem;
import com.pharmaresolve.medcom.repository.WatchlistItemRepository;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import com.pharmaresolve.medcom.service.dto.WatchlistItemDTO;
import com.pharmaresolve.medcom.service.mapper.WatchlistItemMapper;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.pharmaresolve.medcom.service.mapper.WatchlistMapper;
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
    private final WatchlistMapper watchlistMapper;
    private final WatchlistService watchlistService;

    public WatchlistItemService(
        WatchlistItemRepository watchlistItemRepository,
        WatchlistItemMapper watchlistItemMapper, WatchlistMapper watchlistMapper,
        WatchlistService watchlistService
    ) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.watchlistItemMapper = watchlistItemMapper;
        this.watchlistMapper = watchlistMapper;
        this.watchlistService = watchlistService;
    }

    /**
     * Save a watchlistItem.
     *
     * @param watchlistItemDTO the entity to save.
     * @return the persisted entity.
     */
    public WatchlistItemDTO save(WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to save WatchlistItem : {}", watchlistItemDTO);
        WatchlistItem watchlistItem = watchlistItemMapper.toEntity(watchlistItemDTO);
        watchlistItem = watchlistItemRepository.save(watchlistItem);
        return watchlistItemMapper.toDto(watchlistItem);
    }

    /**
     * Add an item to a pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param watchlistItemDTO the watchlist item to add.
     * @return the persisted watchlist item.
     * @throws BadRequestAlertException if watchlist doesn't exist, limit exceeded, or product already exists.
     */
    public WatchlistItemDTO addItemToWatchlist(Long pharmacyId, WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to add WatchlistItem to pharmacy {} watchlist : {}", pharmacyId, watchlistItemDTO);

        // Validate watchlist exists
        WatchlistDTO watchlist = watchlistService.findOne(pharmacyId)
            .orElseThrow(() -> new BadRequestAlertException(
                "Watchlist not found for pharmacy", "watchlist", "watchlistnotfound"));

        // Check if product already exists in this watchlist
        if (watchlistItemDTO.getProduct() != null && watchlistItemDTO.getProduct().getId() != null) {
            boolean productExists = watchlistItemRepository
                .existsByWatchlistIdAndProductId(pharmacyId, watchlistItemDTO.getProduct().getId());
            if (productExists) {
                throw new BadRequestAlertException(
                    "Product already exists in this watchlist", "watchlistItem", "productduplicate");
            }
        }

        // Check watchlist limit
        long currentItemCount = watchlistItemRepository.countByWatchlistId(pharmacyId);
        if (currentItemCount >= watchlist.getLimit()) {
            throw new BadRequestAlertException(
                "Watchlist limit exceeded", "watchlistItem", ErrorConstants.WATCHLIST_LIMIT_EXCEEDED);
        }

        // Set default values
        watchlistItemDTO.setWatchlist(watchlist);
        watchlistItemDTO.setDateAdded(ZonedDateTime.now());
        watchlistItemDTO.setAlertEnabled(true);
        if (watchlistItemDTO.getPriority() == null) {
            watchlistItemDTO.setPriority(1);
        }

        return save(watchlistItemDTO);
    }

    /**
     * Update an item in a pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param itemId the watchlist item ID.
     * @param watchlistItemDTO the updated watchlist item.
     * @return the updated watchlist item.
     * @throws BadRequestAlertException if item doesn't exist or doesn't belong to the pharmacy's watchlist.
     */
    public WatchlistItemDTO updateItemInWatchlist(Long pharmacyId, Long itemId, WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to update WatchlistItem {} in pharmacy {} watchlist : {}", itemId, pharmacyId, watchlistItemDTO);

        // Validate item exists and belongs to the pharmacy's watchlist
        WatchlistItem existingItem = watchlistItemRepository.findByIdAndWatchlistId(itemId, pharmacyId)
            .orElseThrow(() -> new BadRequestAlertException(
                "Watchlist item not found or doesn't belong to this pharmacy", "watchlistItem", "itemnotfound"));

        // Preserve immutable fields
        watchlistItemDTO.setId(itemId);
        watchlistItemDTO.setDateAdded(existingItem.getDateAdded());
        watchlistItemDTO.setAddedBy(existingItem.getAddedBy());
        watchlistItemDTO.setPriority(existingItem.getPriority());
        watchlistItemDTO.setWatchlist(watchlistMapper.toDto(existingItem.getWatchlist()));
        watchlistItemDTO.setProduct(watchlistItemMapper.toDto(existingItem).getProduct());

        return save(watchlistItemDTO);
    }

    /**
     * Remove an item from a pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param itemId the watchlist item ID.
     * @throws BadRequestAlertException if item doesn't exist or doesn't belong to the pharmacy's watchlist.
     */
    public void removeItemFromWatchlist(Long pharmacyId, Long itemId) {
        LOG.debug("Request to remove WatchlistItem {} from pharmacy {} watchlist", itemId, pharmacyId);

        // Validate item exists and belongs to the pharmacy's watchlist
        boolean exists = watchlistItemRepository.findByIdAndWatchlistId(itemId, pharmacyId).isPresent();
        if (!exists) {
            throw new BadRequestAlertException(
                "Watchlist item not found or doesn't belong to this pharmacy", "watchlistItem", "itemnotfound");
        }

        watchlistItemRepository.deleteById(itemId);
    }

    /**
     * Get all items in a pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param pageable the pagination information.
     * @return the list of watchlist items.
     */
    @Transactional(readOnly = true)
    public Page<WatchlistItemDTO> findItemsByWatchlist(Long pharmacyId, Pageable pageable) {
        LOG.debug("Request to get all WatchlistItems for pharmacy {}", pharmacyId);
        return watchlistItemRepository.findByWatchlistId(pharmacyId, pageable)
            .map(watchlistItemMapper::toDto);
    }

    /**
     * Get all the watchlistItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WatchlistItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all WatchlistItems");
        return watchlistItemRepository.findAll(pageable).map(watchlistItemMapper::toDto);
    }

    /**
     * Get one watchlistItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WatchlistItemDTO> findOne(Long id) {
        LOG.debug("Request to get WatchlistItem : {}", id);
        return watchlistItemRepository.findById(id).map(watchlistItemMapper::toDto);
    }

    /**
     * Delete the watchlistItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WatchlistItem : {}", id);
        watchlistItemRepository.deleteById(id);
    }
}
