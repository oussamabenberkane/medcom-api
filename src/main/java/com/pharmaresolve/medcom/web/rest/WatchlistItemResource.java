package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.service.WatchlistItemService;
import com.pharmaresolve.medcom.service.dto.WatchlistItemDTO;
import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.pharmaresolve.medcom.domain.WatchlistItem}.
 */
@RestController
@RequestMapping("/api/pharmacies/{pharmacyId}/watchlist/items")
public class WatchlistItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistItemResource.class);
    private static final String ENTITY_NAME = "watchlistItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WatchlistItemService watchlistItemService;

    public WatchlistItemResource(WatchlistItemService watchlistItemService) {
        this.watchlistItemService = watchlistItemService;
    }

    /**
     * Add a new item to a pharmacy's watchlist.
     */
    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WatchlistItemDTO> addItemToWatchlist(@PathVariable Long pharmacyId, @Valid @RequestBody WatchlistItemDTO watchlistItemDTO) throws URISyntaxException {
        LOG.debug("REST request to add WatchlistItem to pharmacy {} : {}", pharmacyId, watchlistItemDTO);

        if (watchlistItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new watchlist item cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // Validate priority
        if (watchlistItemDTO.getPriority() != null && (watchlistItemDTO.getPriority() < 1 || watchlistItemDTO.getPriority() > 3)) {
            throw new BadRequestAlertException("Priority must be between 1 and 3", ENTITY_NAME, "invalidpriority");
        }

        WatchlistItemDTO createdItem = watchlistItemService.addItemToWatchlist(pharmacyId, watchlistItemDTO);

        return ResponseEntity
            .created(new URI("/api/pharmacies/" + pharmacyId + "/watchlist/items/" + createdItem.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, createdItem.getId().toString()))
            .body(createdItem);
    }

    /**
     * Update an existing watchlist item.
     */
    @PutMapping("/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WatchlistItemDTO> updateItemInWatchlist(@PathVariable Long pharmacyId, @PathVariable Long itemId, @Valid @RequestBody WatchlistItemDTO watchlistItemDTO) {
        LOG.debug("REST request to update WatchlistItem {} in pharmacy {} : {}", itemId, pharmacyId, watchlistItemDTO);

        if (watchlistItemDTO.getId() != null && !Objects.equals(itemId, watchlistItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        // Validate priority
        if (watchlistItemDTO.getPriority() != null && (watchlistItemDTO.getPriority() < 1 || watchlistItemDTO.getPriority() > 3)) {
            throw new BadRequestAlertException("Priority must be between 1 and 3", ENTITY_NAME, "invalidpriority");
        }

        WatchlistItemDTO updatedItem = watchlistItemService.updateItemInWatchlist(pharmacyId, itemId, watchlistItemDTO);

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemId.toString()))
            .body(updatedItem);
    }

    /**
     * Remove an item from a pharmacy's watchlist.
     */
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeItemFromWatchlist(@PathVariable Long pharmacyId, @PathVariable Long itemId) {
        LOG.debug("REST request to remove WatchlistItem {} from pharmacy {}", itemId, pharmacyId);

        watchlistItemService.removeItemFromWatchlist(pharmacyId, itemId);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, itemId.toString()))
            .build();
    }

    /**
     * Get all watchlist items for a pharmacy (paged).
     */
    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<WatchlistItemDTO>> getWatchlistItems(@PathVariable Long pharmacyId, @ParameterObject Pageable pageable) {
        LOG.debug("REST request to get WatchlistItems for pharmacy {}", pharmacyId);

        Page<WatchlistItemDTO> page = watchlistItemService.findItemsByWatchlist(pharmacyId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Get a watchlist item by ID for a pharmacy.
     */
    @GetMapping("/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WatchlistItemDTO> getWatchlistItem(@PathVariable Long pharmacyId, @PathVariable Long itemId) {
        LOG.debug("REST request to get WatchlistItem {} from pharmacy {}", itemId, pharmacyId);

        Optional<WatchlistItemDTO> watchlistItemDTO = watchlistItemService.findItemByIdAndPharmacy(itemId, pharmacyId);
        return ResponseUtil.wrapOrNotFound(watchlistItemDTO);
    }
}
