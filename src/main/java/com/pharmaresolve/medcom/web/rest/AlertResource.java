package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.repository.AlertRepository;
import com.pharmaresolve.medcom.service.AlertService;
import com.pharmaresolve.medcom.service.dto.AlertDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.pharmaresolve.medcom.domain.Alert}.
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlertResource.class);

    private static final String ENTITY_NAME = "alert";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlertService alertService;

    private final AlertRepository alertRepository;

    public AlertResource(AlertService alertService, AlertRepository alertRepository) {
        this.alertService = alertService;
        this.alertRepository = alertRepository;
    }

    /**
     * {@code POST  /alerts} : Create a new alert.
     *
     * @param alertDTO the alertDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alertDTO, or with status {@code 400 (Bad Request)} if the alert has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AlertDTO> createAlert(@RequestBody AlertDTO alertDTO) throws URISyntaxException {
        LOG.debug("REST request to save Alert : {}", alertDTO);
        if (alertDTO.getId() != null) {
            throw new BadRequestAlertException("A new alert cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alertDTO = alertService.save(alertDTO);
        return ResponseEntity.created(new URI("/api/alerts/" + alertDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, alertDTO.getId().toString()))
            .body(alertDTO);
    }

    /**
     * {@code PUT  /alerts/:id} : Updates an existing alert.
     *
     * @param id the id of the alertDTO to save.
     * @param alertDTO the alertDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertDTO,
     * or with status {@code 400 (Bad Request)} if the alertDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alertDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlertDTO> updateAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AlertDTO alertDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Alert : {}, {}", id, alertDTO);
        if (alertDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        alertDTO = alertService.update(alertDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alertDTO.getId().toString()))
            .body(alertDTO);
    }

    /**
     * {@code GET  /alerts} : get all the alerts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of alerts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AlertDTO>> getAllAlerts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Alerts");
        Page<AlertDTO> page = alertService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /alerts/:id} : get the "id" alert.
     *
     * @param id the id of the alertDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alertDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Alert : {}", id);
        Optional<AlertDTO> alertDTO = alertService.findOne(id);
        return ResponseUtil.wrapOrNotFound(alertDTO);
    }

    /**
     * {@code DELETE  /alerts/:id} : delete the "id" alert.
     *
     * @param id the id of the alertDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Alert : {}", id);
        alertService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
