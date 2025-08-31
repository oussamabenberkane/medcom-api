package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.repository.WatchlistItemRepository;
import com.pharmaresolve.medcom.service.WatchlistItemService;
import com.pharmaresolve.medcom.service.dto.WatchlistItemDTO;
import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
     * {@code POST /pharmacies/{pharmacyId}/watchlist/items} : Add an item to the pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param watchlistItemDTO the watchlistItemDTO to add.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new watchlistItemDTO,
     *         or with status {@code 400 (Bad Request)} if validation fails.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WatchlistItemDTO> addItemToWatchlist(
        @PathVariable Long pharmacyId,
        @Valid @RequestBody WatchlistItemDTO watchlistItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to add WatchlistItem to pharmacy {} : {}", pharmacyId, watchlistItemDTO);

        if (watchlistItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new watchlist item cannot already have an ID", ENTITY_NAME, "idexists");
        }

        WatchlistItemDTO result = watchlistItemService.addItemToWatchlist(pharmacyId, watchlistItemDTO);

        return ResponseEntity.created(new URI("/api/pharmacies/" + pharmacyId + "/watchlist/items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT /pharmacies/{pharmacyId}/watchlist/items/{itemId}} : Update an item in the pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param itemId the item ID.
     * @param watchlistItemDTO the watchlistItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated watchlistItemDTO,
     *         or with status {@code 400 (Bad Request)} if validation fails,
     *         or with status {@code 404 (Not Found)} if the item doesn't exist.
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<WatchlistItemDTO> updateItemInWatchlist(
        @PathVariable Long pharmacyId,
        @PathVariable Long itemId,
        @Valid @RequestBody WatchlistItemDTO watchlistItemDTO
    ) {
        LOG.debug("REST request to update WatchlistItem {} in pharmacy {} : {}", itemId, pharmacyId, watchlistItemDTO);

        if (watchlistItemDTO.getId() != null && !Objects.equals(itemId, watchlistItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        WatchlistItemDTO result = watchlistItemService.updateItemInWatchlist(pharmacyId, itemId, watchlistItemDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemId.toString()))
            .body(result);
    }

    /**
     * {@code DELETE /pharmacies/{pharmacyId}/watchlist/items/{itemId}} : Remove an item from the pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param itemId the item ID.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)},
     *         or with status {@code 404 (Not Found)} if the item doesn't exist.
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItemFromWatchlist(
        @PathVariable Long pharmacyId,
        @PathVariable Long itemId
    ) {
        LOG.debug("REST request to remove WatchlistItem {} from pharmacy {}", itemId, pharmacyId);

        watchlistItemService.removeItemFromWatchlist(pharmacyId, itemId);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, itemId.toString()))
            .build();
    }

    /**
     * {@code GET /pharmacies/{pharmacyId}/watchlist/items} : Get all items in the pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of watchlist items in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WatchlistItemDTO>> getWatchlistItems(
        @PathVariable Long pharmacyId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WatchlistItems for pharmacy {}", pharmacyId);

        Page<WatchlistItemDTO> page = watchlistItemService.findItemsByWatchlist(pharmacyId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET /pharmacies/{pharmacyId}/watchlist/items/{itemId}} : Get a specific item from the pharmacy's watchlist.
     *
     * @param pharmacyId the pharmacy ID.
     * @param itemId the item ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the watchlistItemDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<WatchlistItemDTO> getWatchlistItem(
        @PathVariable Long pharmacyId,
        @PathVariable Long itemId
    ) {
        LOG.debug("REST request to get WatchlistItem {} from pharmacy {}", itemId, pharmacyId);

        // First check if the item belongs to this pharmacy's watchlist
        Optional<WatchlistItemDTO> watchlistItemDTO = watchlistItemService.findOne(itemId);

        if (watchlistItemDTO.isEmpty() ||
            !Objects.equals(watchlistItemDTO.get().getWatchlist().getId(), pharmacyId)) {
            throw new BadRequestAlertException(
                "Watchlist item not found or doesn't belong to this pharmacy", ENTITY_NAME, "itemnotfound");
        }

        return ResponseUtil.wrapOrNotFound(watchlistItemDTO);
    }
}
