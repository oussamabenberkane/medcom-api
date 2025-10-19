package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.domain.enumeration.NotificationStatus;
import com.pharmaresolve.medcom.domain.enumeration.NotificationType;
import com.pharmaresolve.medcom.repository.NotificationRepository;
import com.pharmaresolve.medcom.security.SecurityUtils;
import com.pharmaresolve.medcom.service.NotificationService;
import com.pharmaresolve.medcom.service.UserService;
import com.pharmaresolve.medcom.service.dto.NotificationDTO;
import com.pharmaresolve.medcom.service.dto.NotificationHistoryDTO;
import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.pharmaresolve.medcom.domain.Notification}.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    private final UserService userService;

    public NotificationResource(NotificationService notificationService, NotificationRepository notificationRepository, UserService userService) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notificationDTO the notificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationDTO, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) throws URISyntaxException {
        LOG.debug("REST request to save Notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationDTO = notificationService.save(notificationDTO);
        return ResponseEntity.created(new URI("/api/notifications/" + notificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PUT  /notifications/:id} : Updates an existing notification.
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NotificationDTO> updateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notification : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationDTO = notificationService.update(notificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code GET  /notifications} : get notification history for the authenticated user with optional filtering.
     *
     * @param status optional status filter
     * @param type optional notification type filter
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notification history in body
     */
    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificationHistoryDTO>> getNotificationHistory(
        @RequestParam(required = false) NotificationStatus status,
        @RequestParam(required = false) NotificationType type,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get notification history for authenticated user with filters - status: {}, type: {}", status, type);

        // Get authenticated user's email
        Optional<String> userEmail = SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userService.getUserWithAuthoritiesByLogin(login))
            .map(user -> user.getEmail());

        if (userEmail.isEmpty()) {
            LOG.error("Could not get authenticated user email for notification history");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<NotificationHistoryDTO> page = notificationService.getNotificationHistoryForUser(
            userEmail.get(), status, type, pageable
        );

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(), page
        );

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications/item/:itemId} : get notification history for authenticated user for a specific watchlist item.
     *
     * @param itemId the watchlist item ID
     * @param status optional status filter
     * @param type optional notification type filter
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notification history in body
     */
    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificationHistoryDTO>> getNotificationHistoryForItem(
        @PathVariable("itemId") Long itemId,
        @RequestParam(required = false) NotificationStatus status,
        @RequestParam(required = false) NotificationType type,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get notification history for authenticated user for watchlist item: {} with filters - status: {}, type: {}",
            itemId, status, type);

        // Get authenticated user's email
        Optional<String> userEmail = SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userService.getUserWithAuthoritiesByLogin(login))
            .map(user -> user.getEmail());

        if (userEmail.isEmpty()) {
            LOG.error("Could not get authenticated user email for notification history");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<NotificationHistoryDTO> page = notificationService.getNotificationHistoryForUserAndItem(
            userEmail.get(), itemId, status, type, pageable
        );

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(), page
        );

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code PUT  /notifications/:id/mark-as-read} : Mark a notification as read.
     *
     * @param id the notification ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated notification, or {@code 404 (Not Found)}
     */
    @PutMapping("/{id}/mark-as-read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@PathVariable("id") Long id) {
        LOG.debug("REST request to mark notification {} as read", id);

        // Get authenticated user's email
        Optional<String> userEmail = SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userService.getUserWithAuthoritiesByLogin(login))
            .map(user -> user.getEmail());

        if (userEmail.isEmpty()) {
            LOG.error("Could not get authenticated user email for marking notification as read");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<NotificationDTO> result = notificationService.markAsRead(id, userEmail.get());

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result.get());
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notification : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notification : {}", id);
        notificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
