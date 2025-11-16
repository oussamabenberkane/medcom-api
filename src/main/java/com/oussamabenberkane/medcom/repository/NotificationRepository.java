package com.oussamabenberkane.medcom.repository;

import com.oussamabenberkane.medcom.domain.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    @Query("select notification from Notification notification where notification.user.login = ?#{authentication.name}")
    List<Notification> findByUserIsCurrentUser();

    List<Notification> findAllByEmailSentFalse();

    Page<Notification> findByUserLoginOrderByCreatedAtDesc(String userLogin, Pageable pageable);

    Page<Notification> findByUserLoginAndWatchlistItemIdOrderByCreatedAtDesc(String userLogin, Long watchlistItemId, Pageable pageable);
}
