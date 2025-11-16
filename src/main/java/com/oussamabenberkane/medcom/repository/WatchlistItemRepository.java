package com.oussamabenberkane.medcom.repository;

import com.oussamabenberkane.medcom.domain.WatchlistItem;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WatchlistItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long>, JpaSpecificationExecutor<WatchlistItem> {
    List<WatchlistItem> findAllByWatchlistPharmacyId(Long pharmacyId);

    long countByWatchlistId(Long watchlistId);
}
