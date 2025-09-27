package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Alert;
import com.pharmaresolve.medcom.repository.AlertRepository;
import com.pharmaresolve.medcom.service.dto.AlertDTO;
import com.pharmaresolve.medcom.service.mapper.AlertMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.pharmaresolve.medcom.domain.Alert}.
 */
@Service
@Transactional
public class AlertService {

    private static final Logger LOG = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;

    private final AlertMapper alertMapper;

    public AlertService(AlertRepository alertRepository, AlertMapper alertMapper) {
        this.alertRepository = alertRepository;
        this.alertMapper = alertMapper;
    }

    /**
     * Save a alert.
     *
     * @param alertDTO the entity to save.
     * @return the persisted entity.
     */
    public AlertDTO save(AlertDTO alertDTO) {
        LOG.debug("Request to save Alert : {}", alertDTO);
        Alert alert = alertMapper.toEntity(alertDTO);
        alert = alertRepository.save(alert);
        return alertMapper.toDto(alert);
    }

    /**
     * Update a alert.
     *
     * @param alertDTO the entity to save.
     * @return the persisted entity.
     */
    public AlertDTO update(AlertDTO alertDTO) {
        LOG.debug("Request to update Alert : {}", alertDTO);
        Alert alert = alertMapper.toEntity(alertDTO);
        alert = alertRepository.save(alert);
        return alertMapper.toDto(alert);
    }

    /**
     * Get all the alerts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AlertDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Alerts");
        return alertRepository.findAll(pageable).map(alertMapper::toDto);
    }

    /**
     * Get one alert by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AlertDTO> findOne(Long id) {
        LOG.debug("Request to get Alert : {}", id);
        return alertRepository.findById(id).map(alertMapper::toDto);
    }

    /**
     * Delete the alert by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Alert : {}", id);
        alertRepository.deleteById(id);
    }
}
