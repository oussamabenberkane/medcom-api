package com.pharmaresolve.medcom.repository;

import com.pharmaresolve.medcom.domain.Notification;
import com.pharmaresolve.medcom.domain.enumeration.NotificationStatus;
import com.pharmaresolve.medcom.domain.enumeration.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findByMailjetMessageId(String mailjetMessageId);

    /**
     * Find notifications by recipient email with optional filtering.
     */
    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.alert a " +
           "LEFT JOIN FETCH a.watchlistItem wi " +
           "LEFT JOIN FETCH wi.product p " +
           "WHERE n.recipientEmail = :recipientEmail " +
           "AND (:status IS NULL OR n.status = :status) " +
           "AND (:type IS NULL OR n.type = :type) " +
           "ORDER BY n.sentAt DESC NULLS LAST, n.id DESC")
    Page<Notification> findByRecipientEmailWithFilters(
        @Param("recipientEmail") String recipientEmail,
        @Param("status") NotificationStatus status,
        @Param("type") NotificationType type,
        Pageable pageable
    );

    /**
     * Find notifications by recipient email and watchlist item with optional filtering.
     */
    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.alert a " +
           "LEFT JOIN FETCH a.watchlistItem wi " +
           "LEFT JOIN FETCH wi.product p " +
           "WHERE n.recipientEmail = :recipientEmail " +
           "AND wi.id = :watchlistItemId " +
           "AND (:status IS NULL OR n.status = :status) " +
           "AND (:type IS NULL OR n.type = :type) " +
           "ORDER BY n.sentAt DESC NULLS LAST, n.id DESC")
    Page<Notification> findByRecipientEmailAndWatchlistItemWithFilters(
        @Param("recipientEmail") String recipientEmail,
        @Param("watchlistItemId") Long watchlistItemId,
        @Param("status") NotificationStatus status,
        @Param("type") NotificationType type,
        Pageable pageable
    );

    /**
     * Find a notification by ID and recipient email (for authorization check).
     */
    Optional<Notification> findByIdAndRecipientEmail(Long id, String recipientEmail);
}
