package com.oussamabenberkane.medcom.web.rest;

import com.oussamabenberkane.medcom.repository.WatchlistRepository;
import com.oussamabenberkane.medcom.service.WatchlistService;
import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import com.oussamabenberkane.medcom.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.oussamabenberkane.medcom.domain.Watchlist}.
 */
@RestController
@RequestMapping("/api/watchlists")
public class WatchlistResource {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistResource.class);

    private static final String ENTITY_NAME = "watchlist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WatchlistService watchlistService;

    private final WatchlistRepository watchlistRepository;

    public WatchlistResource(WatchlistService watchlistService, WatchlistRepository watchlistRepository) {
        this.watchlistService = watchlistService;
        this.watchlistRepository = watchlistRepository;
    }

    /**
     * {@code POST  /watchlists} : Create a new watchlist.
     *
     * @param watchlistDTO the watchlistDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new watchlistDTO, or with status {@code 400 (Bad Request)} if the watchlist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WatchlistDTO> createWatchlist(@Valid @RequestBody WatchlistDTO watchlistDTO) throws URISyntaxException {
        LOG.debug("REST request to save Watchlist : {}", watchlistDTO);
        if (watchlistDTO.getId() != null) {
            throw new BadRequestAlertException("A new watchlist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        watchlistDTO = watchlistService.save(watchlistDTO);
        return ResponseEntity.created(new URI("/api/watchlists/" + watchlistDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, watchlistDTO.getId().toString()))
            .body(watchlistDTO);
    }

    /**
     * {@code PUT  /watchlists/:id} : Updates an existing watchlist.
     *
     * @param id the id of the watchlistDTO to save.
     * @param watchlistDTO the watchlistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated watchlistDTO,
     * or with status {@code 400 (Bad Request)} if the watchlistDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the watchlistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WatchlistDTO> updateWatchlist(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WatchlistDTO watchlistDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Watchlist : {}, {}", id, watchlistDTO);
        if (watchlistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, watchlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!watchlistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        watchlistDTO = watchlistService.update(watchlistDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, watchlistDTO.getId().toString()))
            .body(watchlistDTO);
    }

    /**
     * {@code PATCH  /watchlists/:id} : Partial updates given fields of an existing watchlist, field will ignore if it is null
     *
     * @param id the id of the watchlistDTO to save.
     * @param watchlistDTO the watchlistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated watchlistDTO,
     * or with status {@code 400 (Bad Request)} if the watchlistDTO is not valid,
     * or with status {@code 404 (Not Found)} if the watchlistDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the watchlistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WatchlistDTO> partialUpdateWatchlist(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WatchlistDTO watchlistDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Watchlist partially : {}, {}", id, watchlistDTO);
        if (watchlistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, watchlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!watchlistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WatchlistDTO> result = watchlistService.partialUpdate(watchlistDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, watchlistDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /watchlists} : get all the watchlists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of watchlists in body.
     */
    @GetMapping("")
    public List<WatchlistDTO> getAllWatchlists() {
        LOG.debug("REST request to get all Watchlists");
        return watchlistService.findAll();
    }

    /**
     * {@code GET  /watchlists/:id} : get the "id" watchlist.
     *
     * @param id the id of the watchlistDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the watchlistDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WatchlistDTO> getWatchlist(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Watchlist : {}", id);
        Optional<WatchlistDTO> watchlistDTO = watchlistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(watchlistDTO);
    }

    /**
     * {@code DELETE  /watchlists/:id} : delete the "id" watchlist.
     *
     * @param id the id of the watchlistDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Watchlist : {}", id);
        watchlistService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
