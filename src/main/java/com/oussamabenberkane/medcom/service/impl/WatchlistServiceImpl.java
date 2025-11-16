package com.oussamabenberkane.medcom.service.impl;

import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.repository.WatchlistRepository;
import com.oussamabenberkane.medcom.service.WatchlistService;
import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import com.oussamabenberkane.medcom.service.mapper.WatchlistMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.oussamabenberkane.medcom.domain.Watchlist}.
 */
@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistServiceImpl.class);

    private final WatchlistRepository watchlistRepository;

    private final WatchlistMapper watchlistMapper;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, WatchlistMapper watchlistMapper) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistMapper = watchlistMapper;
    }

    @Override
    public WatchlistDTO save(WatchlistDTO watchlistDTO) {
        LOG.debug("Request to save Watchlist : {}", watchlistDTO);
        Watchlist watchlist = watchlistMapper.toEntity(watchlistDTO);
        watchlist = watchlistRepository.save(watchlist);
        return watchlistMapper.toDto(watchlist);
    }

    @Override
    public WatchlistDTO update(WatchlistDTO watchlistDTO) {
        LOG.debug("Request to update Watchlist : {}", watchlistDTO);
        Watchlist watchlist = watchlistMapper.toEntity(watchlistDTO);
        watchlist = watchlistRepository.save(watchlist);
        return watchlistMapper.toDto(watchlist);
    }

    @Override
    public Optional<WatchlistDTO> partialUpdate(WatchlistDTO watchlistDTO) {
        LOG.debug("Request to partially update Watchlist : {}", watchlistDTO);

        return watchlistRepository
            .findById(watchlistDTO.getId())
            .map(existingWatchlist -> {
                watchlistMapper.partialUpdate(existingWatchlist, watchlistDTO);

                return existingWatchlist;
            })
            .map(watchlistRepository::save)
            .map(watchlistMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchlistDTO> findAll() {
        LOG.debug("Request to get all Watchlists");
        return watchlistRepository.findAll().stream().map(watchlistMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WatchlistDTO> findOne(Long id) {
        LOG.debug("Request to get Watchlist : {}", id);
        return watchlistRepository.findById(id).map(watchlistMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Watchlist : {}", id);
        watchlistRepository.deleteById(id);
    }
}
