package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.repository.WatchlistRepository;
import com.pharmaresolve.medcom.service.WatchlistService;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.pharmaresolve.medcom.domain.Watchlist}.
 */
@RestController
@RequestMapping("/api/watchlists")
public class WatchlistResource {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistResource.class);
    private static final String ENTITY_NAME = "watchlist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WatchlistService watchlistService;

    public WatchlistResource(WatchlistService watchlistService, WatchlistRepository watchlistRepository) {
        this.watchlistService = watchlistService;
    }

    /**
     * Update the watchlist for a pharmacy.
     */
    @PutMapping("/{pharmacyId}")
    public ResponseEntity<WatchlistDTO> updateWatchlistForPharmacy(@PathVariable Long pharmacyId, @RequestBody WatchlistDTO watchlistDTO) {
        LOG.debug("REST request to update Watchlist for pharmacy {} : {}", pharmacyId, watchlistDTO);

        if (watchlistDTO.getId() != null && !Objects.equals(pharmacyId, watchlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        WatchlistDTO updatedWatchlist = watchlistService.update(pharmacyId, watchlistDTO);

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pharmacyId.toString()))
            .body(updatedWatchlist);
    }

    /**
     * Get all watchlists (paged).
     */
    @GetMapping("")
    public ResponseEntity<List<WatchlistDTO>> getAllWatchlists(@ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Watchlists");
        Page<WatchlistDTO> page = watchlistService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Get a watchlist by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WatchlistDTO> getWatchlist(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Watchlist : {}", id);
        Optional<WatchlistDTO> watchlistDTO = watchlistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(watchlistDTO);
    }
}
