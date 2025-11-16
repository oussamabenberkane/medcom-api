package com.oussamabenberkane.medcom.web.rest;

import com.oussamabenberkane.medcom.domain.Notification;
import com.oussamabenberkane.medcom.repository.NotificationRepository;
import com.oussamabenberkane.medcom.repository.WatchlistItemRepository;
import com.oussamabenberkane.medcom.security.SecurityUtils;
import com.oussamabenberkane.medcom.service.WatchlistItemQueryService;
import com.oussamabenberkane.medcom.service.WatchlistItemService;
import com.oussamabenberkane.medcom.service.criteria.WatchlistItemCriteria;
import com.oussamabenberkane.medcom.service.dto.NotificationDTO;
import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import com.oussamabenberkane.medcom.service.mapper.NotificationMapper;
import com.oussamabenberkane.medcom.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.oussamabenberkane.medcom.domain.WatchlistItem}.
 */
@RestController
@RequestMapping("/api/watchlist-items")
public class WatchlistItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistItemResource.class);

    private static final String ENTITY_NAME = "watchlistItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WatchlistItemService watchlistItemService;

    private final WatchlistItemRepository watchlistItemRepository;

    private final WatchlistItemQueryService watchlistItemQueryService;

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public WatchlistItemResource(
        WatchlistItemService watchlistItemService,
        WatchlistItemRepository watchlistItemRepository,
        WatchlistItemQueryService watchlistItemQueryService,
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper
    ) {
        this.watchlistItemService = watchlistItemService;
        this.watchlistItemRepository = watchlistItemRepository;
        this.watchlistItemQueryService = watchlistItemQueryService;
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * {@code POST  /watchlist-items} : Create a new watchlistItem.
     *
     * @param watchlistItemDTO the watchlistItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new watchlistItemDTO, or with status {@code 400 (Bad Request)} if the watchlistItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WatchlistItemDTO> createWatchlistItem(@Valid @RequestBody WatchlistItemDTO watchlistItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save WatchlistItem : {}", watchlistItemDTO);
        if (watchlistItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new watchlistItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        watchlistItemDTO = watchlistItemService.save(watchlistItemDTO);
        return ResponseEntity.created(new URI("/api/watchlist-items/" + watchlistItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, watchlistItemDTO.getId().toString()))
            .body(watchlistItemDTO);
    }

    /**
     * {@code PUT  /watchlist-items/:id} : Updates an existing watchlistItem.
     *
     * @param id the id of the watchlistItemDTO to save.
     * @param watchlistItemDTO the watchlistItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated watchlistItemDTO,
     * or with status {@code 400 (Bad Request)} if the watchlistItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the watchlistItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WatchlistItemDTO> updateWatchlistItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WatchlistItemDTO watchlistItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WatchlistItem : {}, {}", id, watchlistItemDTO);
        if (watchlistItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, watchlistItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!watchlistItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        watchlistItemDTO = watchlistItemService.update(watchlistItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, watchlistItemDTO.getId().toString()))
            .body(watchlistItemDTO);
    }

    /**
     * {@code PATCH  /watchlist-items/:id} : Partial updates given fields of an existing watchlistItem, field will ignore if it is null
     *
     * @param id the id of the watchlistItemDTO to save.
     * @param watchlistItemDTO the watchlistItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated watchlistItemDTO,
     * or with status {@code 400 (Bad Request)} if the watchlistItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the watchlistItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the watchlistItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WatchlistItemDTO> partialUpdateWatchlistItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WatchlistItemDTO watchlistItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WatchlistItem partially : {}, {}", id, watchlistItemDTO);
        if (watchlistItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, watchlistItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!watchlistItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WatchlistItemDTO> result = watchlistItemService.partialUpdate(watchlistItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, watchlistItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /watchlist-items} : get all the watchlistItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of watchlistItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WatchlistItemDTO>> getAllWatchlistItems(
        WatchlistItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WatchlistItems by criteria: {}", criteria);

        Page<WatchlistItemDTO> page = watchlistItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /watchlist-items/count} : count all the watchlistItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWatchlistItems(WatchlistItemCriteria criteria) {
        LOG.debug("REST request to count WatchlistItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(watchlistItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /watchlist-items/:id} : get the "id" watchlistItem.
     *
     * @param id the id of the watchlistItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the watchlistItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WatchlistItemDTO> getWatchlistItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WatchlistItem : {}", id);
        Optional<WatchlistItemDTO> watchlistItemDTO = watchlistItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(watchlistItemDTO);
    }

    /**
     * {@code DELETE  /watchlist-items/:id} : delete the "id" watchlistItem.
     *
     * @param id the id of the watchlistItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlistItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WatchlistItem : {}", id);
        watchlistItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /watchlist-items/:id/history} : Get notification history for a specific watchlist item for the current user.
     *
     * @param id the id of the watchlist item.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<NotificationDTO>> getWatchlistItemNotificationHistory(
        @PathVariable("id") Long id,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("No authenticated user found"));

        LOG.debug("REST request to get notification history for watchlist item {} and user: {}", id, currentUserLogin);

        Page<Notification> page = notificationRepository.findByUserLoginAndWatchlistItemIdOrderByCreatedAtDesc(
            currentUserLogin,
            id,
            pageable
        );

        List<NotificationDTO> notificationDTOs = page.getContent().stream().map(notificationMapper::toDto).collect(Collectors.toList());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            new PageImpl<>(notificationDTOs, pageable, page.getTotalElements())
        );

        return ResponseEntity.ok().headers(headers).body(notificationDTOs);
    }
}
