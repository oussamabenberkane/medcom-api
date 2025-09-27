package com.pharmaresolve.medcom.repository;

import com.pharmaresolve.medcom.domain.Notification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findByMailjetMessageId(String mailjetMessageId);
}
