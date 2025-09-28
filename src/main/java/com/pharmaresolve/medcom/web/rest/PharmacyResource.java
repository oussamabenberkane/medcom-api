package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.repository.PharmacyRepository;
import com.pharmaresolve.medcom.service.PharmacyService;
import com.pharmaresolve.medcom.service.ProductAvailabilityMonitoringService;
import com.pharmaresolve.medcom.service.dto.PharmacyDTO;
import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;

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
    private final ProductAvailabilityMonitoringService monitoringService;

    public PharmacyResource(PharmacyService pharmacyService, PharmacyRepository pharmacyRepository, ProductAvailabilityMonitoringService monitoringService) {
        this.pharmacyService = pharmacyService;
        this.pharmacyRepository = pharmacyRepository;
        this.monitoringService = monitoringService;
    }

    /**
     * Create a new pharmacy with its watchlist.
     */
    @PostMapping("")
    public ResponseEntity<PharmacyDTO> createPharmacy(@RequestBody PharmacyDTO pharmacyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pharmacy : {}", pharmacyDTO);

        if (pharmacyDTO.getId() != null) {
            throw new BadRequestAlertException("A new pharmacy cannot already have an ID", ENTITY_NAME, "idexists");
        }

        PharmacyDTO createdPharmacy = pharmacyService.create(pharmacyDTO);
        return ResponseEntity
            .created(new URI("/api/pharmacies/" + createdPharmacy.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, createdPharmacy.getId().toString()))
            .body(createdPharmacy);
    }

    /**
     * Update an existing pharmacy by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PharmacyDTO> updatePharmacy(@PathVariable("id") Long id, @RequestBody PharmacyDTO pharmacyDTO) throws URISyntaxException {
        LOG.debug("REST request to update Pharmacy : {}, {}", id, pharmacyDTO);

        if (pharmacyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pharmacyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        PharmacyDTO updatedPharmacy = pharmacyService.update(pharmacyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, updatedPharmacy.getId().toString()))
            .body(updatedPharmacy);
    }

    /**
     * Get all pharmacies with pagination, or filter by watchlist null.
     */
    @GetMapping("")
    public ResponseEntity<List<PharmacyDTO>> getAllPharmacies(@ParameterObject Pageable pageable, @RequestParam(name = "filter", required = false) String filter) {
        LOG.debug("REST request to get a page of Pharmacies");
        Page<PharmacyDTO> page = pharmacyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Get a single pharmacy by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDTO> getPharmacy(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Pharmacy : {}", id);
        Optional<PharmacyDTO> pharmacyDTO = pharmacyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pharmacyDTO);
    }

    /**
     * Manually trigger availability check for all watchlist items of a pharmacy.
     * Processes all alert-enabled items regardless of priority and sends consolidated emails immediately.
     *
     * @param pharmacyId the pharmacy ID to trigger availability check for
     * @return processing results including number of items processed and alerts created
     */
    @PostMapping("/{pharmacyId}/trigger-availability-check")
    public ResponseEntity<TriggerAvailabilityCheckResponse> triggerAvailabilityCheck(@PathVariable("pharmacyId") Long pharmacyId) {
        LOG.debug("REST request to trigger availability check for pharmacy: {}", pharmacyId);

        try {
            // Verify pharmacy exists
            Optional<PharmacyDTO> pharmacy = pharmacyService.findOne(pharmacyId);
            if (pharmacy.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Trigger the availability check
            ProductAvailabilityMonitoringService.PharmacyMonitoringResult result =
                monitoringService.processAvailabilityForPharmacy(pharmacyId);

            TriggerAvailabilityCheckResponse response = new TriggerAvailabilityCheckResponse(
                result.getPharmacyId(),
                result.getProcessedItems(),
                result.getAlertsCreated(),
                String.format("Successfully processed %d items for pharmacy %d. Created %d alerts and sent consolidated emails.",
                    result.getProcessedItems(), result.getPharmacyId(), result.getAlertsCreated())
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOG.error("Error triggering availability check for pharmacy: {}", pharmacyId, e);
            TriggerAvailabilityCheckResponse errorResponse = new TriggerAvailabilityCheckResponse(
                pharmacyId, 0, 0, "Error triggering availability check: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Delete a pharmacy by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePharmacy(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Pharmacy : {}", id);
        pharmacyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * Response DTO for trigger availability check operations.
     */
    public static class TriggerAvailabilityCheckResponse {
        private Long pharmacyId;
        private int processedItems;
        private int alertsCreated;
        private String message;

        public TriggerAvailabilityCheckResponse() {}

        public TriggerAvailabilityCheckResponse(Long pharmacyId, int processedItems, int alertsCreated, String message) {
            this.pharmacyId = pharmacyId;
            this.processedItems = processedItems;
            this.alertsCreated = alertsCreated;
            this.message = message;
        }

        public Long getPharmacyId() {
            return pharmacyId;
        }

        public void setPharmacyId(Long pharmacyId) {
            this.pharmacyId = pharmacyId;
        }

        public int getProcessedItems() {
            return processedItems;
        }

        public void setProcessedItems(int processedItems) {
            this.processedItems = processedItems;
        }

        public int getAlertsCreated() {
            return alertsCreated;
        }

        public void setAlertsCreated(int alertsCreated) {
            this.alertsCreated = alertsCreated;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TriggerAvailabilityCheckResponse{" +
                "pharmacyId=" + pharmacyId +
                ", processedItems=" + processedItems +
                ", alertsCreated=" + alertsCreated +
                ", message='" + message + '\'' +
                '}';
        }
    }
}
