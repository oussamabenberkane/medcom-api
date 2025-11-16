package com.oussamabenberkane.medcom.service.impl;

import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.repository.WatchlistItemRepository;
import com.oussamabenberkane.medcom.service.WatchlistItemService;
import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import com.oussamabenberkane.medcom.service.mapper.WatchlistItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.oussamabenberkane.medcom.domain.WatchlistItem}.
 */
@Service
@Transactional
public class WatchlistItemServiceImpl implements WatchlistItemService {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistItemServiceImpl.class);

    @Value("${application.watchlist.max-items:10}")
    private int maxWatchlistItems;

    private final WatchlistItemRepository watchlistItemRepository;

    private final WatchlistItemMapper watchlistItemMapper;

    public WatchlistItemServiceImpl(WatchlistItemRepository watchlistItemRepository, WatchlistItemMapper watchlistItemMapper) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.watchlistItemMapper = watchlistItemMapper;
    }

    @Override
    public WatchlistItemDTO save(WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to save WatchlistItem : {}", watchlistItemDTO);

        // Check watchlist item limit
        if (watchlistItemDTO.getWatchlist() != null && watchlistItemDTO.getWatchlist().getId() != null) {
            long currentCount = watchlistItemRepository.countByWatchlistId(watchlistItemDTO.getWatchlist().getId());
            if (currentCount >= maxWatchlistItems) {
                throw new IllegalStateException(String.format("Watchlist has reached the maximum limit of %d items", maxWatchlistItems));
            }
        }

        WatchlistItem watchlistItem = watchlistItemMapper.toEntity(watchlistItemDTO);
        watchlistItem = watchlistItemRepository.save(watchlistItem);
        return watchlistItemMapper.toDto(watchlistItem);
    }

    @Override
    public WatchlistItemDTO update(WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to update WatchlistItem : {}", watchlistItemDTO);
        WatchlistItem watchlistItem = watchlistItemMapper.toEntity(watchlistItemDTO);
        watchlistItem = watchlistItemRepository.save(watchlistItem);
        return watchlistItemMapper.toDto(watchlistItem);
    }

    @Override
    public Optional<WatchlistItemDTO> partialUpdate(WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("Request to partially update WatchlistItem : {}", watchlistItemDTO);

        return watchlistItemRepository
            .findById(watchlistItemDTO.getId())
            .map(existingWatchlistItem -> {
                watchlistItemMapper.partialUpdate(existingWatchlistItem, watchlistItemDTO);

                return existingWatchlistItem;
            })
            .map(watchlistItemRepository::save)
            .map(watchlistItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WatchlistItemDTO> findOne(Long id) {
        LOG.debug("Request to get WatchlistItem : {}", id);
        return watchlistItemRepository.findById(id).map(watchlistItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete WatchlistItem : {}", id);
        watchlistItemRepository.deleteById(id);
    }
}
