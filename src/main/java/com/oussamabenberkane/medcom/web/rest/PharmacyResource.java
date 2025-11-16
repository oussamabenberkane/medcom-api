package com.oussamabenberkane.medcom.web.rest;

import com.oussamabenberkane.medcom.repository.PharmacyRepository;
import com.oussamabenberkane.medcom.service.PharmacyQueryService;
import com.oussamabenberkane.medcom.service.PharmacyService;
import com.oussamabenberkane.medcom.service.ProductAvailabilityMonitoringService;
import com.oussamabenberkane.medcom.service.criteria.PharmacyCriteria;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import com.oussamabenberkane.medcom.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.oussamabenberkane.medcom.domain.Pharmacy}.
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

    private final PharmacyQueryService pharmacyQueryService;

    private final ProductAvailabilityMonitoringService monitoringService;

    public PharmacyResource(
        PharmacyService pharmacyService,
        PharmacyRepository pharmacyRepository,
        PharmacyQueryService pharmacyQueryService,
        ProductAvailabilityMonitoringService monitoringService
    ) {
        this.pharmacyService = pharmacyService;
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyQueryService = pharmacyQueryService;
        this.monitoringService = monitoringService;
    }

    /**
     * {@code POST  /pharmacies} : Create a new pharmacy.
     *
     * @param pharmacyDTO the pharmacyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pharmacyDTO, or with status {@code 400 (Bad Request)} if the pharmacy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PharmacyDTO> createPharmacy(@Valid @RequestBody PharmacyDTO pharmacyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pharmacy : {}", pharmacyDTO);
        if (pharmacyDTO.getId() != null) {
            throw new BadRequestAlertException("A new pharmacy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pharmacyDTO = pharmacyService.save(pharmacyDTO);
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
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pharmacies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PharmacyDTO>> getAllPharmacies(
        PharmacyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Pharmacies by criteria: {}", criteria);

        Page<PharmacyDTO> page = pharmacyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pharmacies/count} : count all the pharmacies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPharmacies(PharmacyCriteria criteria) {
        LOG.debug("REST request to count Pharmacies by criteria: {}", criteria);
        return ResponseEntity.ok().body(pharmacyQueryService.countByCriteria(criteria));
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

    /**
     * {@code POST  /pharmacies/:id/check-availability} : Manually trigger availability check for pharmacy's watchlist.
     *
     * @param id the id of the pharmacy
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body with check results.
     */
    @PostMapping("/{id}/check-availability")
    public ResponseEntity<Map<String, Object>> checkPharmacyWatchlistAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to manually check availability for pharmacy : {}", id);

        try {
            int checkedCount = monitoringService.checkPharmacyWatchlist(id);

            Map<String, Object> response = new HashMap<>();
            response.put("pharmacyId", id);
            response.put("itemsChecked", checkedCount);
            response.put("status", "success");
            response.put("message", String.format("Successfully checked %d watchlist items", checkedCount));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error checking pharmacy watchlist: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("pharmacyId", id);
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
