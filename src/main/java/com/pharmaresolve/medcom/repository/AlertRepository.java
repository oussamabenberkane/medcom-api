package com.pharmaresolve.medcom.repository;

import com.pharmaresolve.medcom.domain.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Alert entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    Page<Alert> findByWatchlistItemWatchlistIdOrderByCreatedDesc(Long watchlistId, Pageable pageable);

    Alert findByMailjetMessageId(String mailjetMessageId);
}
