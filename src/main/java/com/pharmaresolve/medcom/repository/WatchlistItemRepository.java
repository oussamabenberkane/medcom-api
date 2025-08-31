package com.pharmaresolve.medcom.repository;

import com.pharmaresolve.medcom.domain.WatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the WatchlistItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    boolean existsByWatchlistIdAndProductId(Long watchlistId, Long productId);

    long countByWatchlistId(Long watchlistId);

    boolean existsByIdAndWatchlistId(Long itemId, Long watchlistId);

    Optional<WatchlistItem> findByIdAndWatchlistId(Long itemId, Long watchlistId);

    Page<WatchlistItem> findByWatchlistId(Long watchlistId, Pageable pageable);
}
