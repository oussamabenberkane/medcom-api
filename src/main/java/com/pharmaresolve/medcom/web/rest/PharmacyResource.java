package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.repository.PharmacyRepository;
import com.pharmaresolve.medcom.service.PharmacyService;
import com.pharmaresolve.medcom.service.dto.PharmacyDTO;
import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.pharmaresolve.medcom.domain.Pharmacy}.
 */
@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyResource {

    private static final Logger LOG = LoggerFactory.getLogger(PharmacyResource.class);

    private static final String ENTITY_NAME = "pharmacy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PharmacyService pharmacyService;

    private final PharmacyRepository pharmacyRepository;

    public PharmacyResource(PharmacyService pharmacyService, PharmacyRepository pharmacyRepository) {
        this.pharmacyService = pharmacyService;
        this.pharmacyRepository = pharmacyRepository;
    }

    /**
     * {@code POST  /pharmacies} : Create a new pharmacy.
     *
     * @param pharmacyDTO the pharmacyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pharmacyDTO, or with status {@code 400 (Bad Request)} if the pharmacy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PharmacyDTO> createPharmacy(@RequestBody PharmacyDTO pharmacyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pharmacy : {}", pharmacyDTO);
        if (pharmacyDTO.getId() != null) {
            throw new BadRequestAlertException("A new pharmacy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pharmacyDTO = pharmacyService.create(pharmacyDTO);
        return ResponseEntity.created(new URI("/api/pharmacies/" + pharmacyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pharmacyDTO.getId().toString()))
            .body(pharmacyDTO);
    }

    /**
     * {@code PUT  /pharmacies/:id} : Updates an existing pharmacy.
     *
     * @param id the id of the pharmacyDTO to save.
     * @param pharmacyDTO the pharmacyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pharmacyDTO,
     * or with status {@code 400 (Bad Request)} if the pharmacyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pharmacyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PharmacyDTO> updatePharmacy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PharmacyDTO pharmacyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Pharmacy : {}, {}", id, pharmacyDTO);
        if (pharmacyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pharmacyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pharmacyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pharmacyDTO = pharmacyService.update(pharmacyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pharmacyDTO.getId().toString()))
            .body(pharmacyDTO);
    }

    /**
     * {@code PATCH  /pharmacies/:id} : Partial updates given fields of an existing pharmacy, field will ignore if it is null
     *
     * @param id the id of the pharmacyDTO to save.
     * @param pharmacyDTO the pharmacyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pharmacyDTO,
     * or with status {@code 400 (Bad Request)} if the pharmacyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pharmacyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pharmacyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PharmacyDTO> partialUpdatePharmacy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PharmacyDTO pharmacyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Pharmacy partially : {}, {}", id, pharmacyDTO);
        if (pharmacyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pharmacyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pharmacyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PharmacyDTO> result = pharmacyService.partialUpdate(pharmacyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pharmacyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pharmacies} : get all the pharmacies.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pharmacies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PharmacyDTO>> getAllPharmacies(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("watchlist-is-null".equals(filter)) {
            LOG.debug("REST request to get all Pharmacys where watchlist is null");
            return new ResponseEntity<>(pharmacyService.findAllWhereWatchlistIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Pharmacies");
        Page<PharmacyDTO> page = pharmacyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pharmacies/:id} : get the "id" pharmacy.
     *
     * @param id the id of the pharmacyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pharmacyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDTO> getPharmacy(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Pharmacy : {}", id);
        Optional<PharmacyDTO> pharmacyDTO = pharmacyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pharmacyDTO);
    }

    /**
     * {@code DELETE  /pharmacies/:id} : delete the "id" pharmacy.
     *
     * @param id the id of the pharmacyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePharmacy(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Pharmacy : {}", id);
        pharmacyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
