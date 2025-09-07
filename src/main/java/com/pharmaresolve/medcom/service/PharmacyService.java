package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Pharmacy;
import com.pharmaresolve.medcom.domain.Watchlist;
import com.pharmaresolve.medcom.repository.PharmacyRepository;
import com.pharmaresolve.medcom.repository.WatchlistRepository;
import com.pharmaresolve.medcom.service.dto.PharmacyDTO;
import com.pharmaresolve.medcom.service.mapper.PharmacyMapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pharmaresolve.medcom.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.pharmaresolve.medcom.domain.Pharmacy}.
 */
@Service
@Transactional
public class PharmacyService {

    private static final Logger LOG = LoggerFactory.getLogger(PharmacyService.class);

    private final PharmacyRepository pharmacyRepository;
    private final PharmacyMapper pharmacyMapper;
    private final WatchlistRepository watchlistRepository;

    public PharmacyService(PharmacyRepository pharmacyRepository, PharmacyMapper pharmacyMapper, WatchlistRepository watchlistRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyMapper = pharmacyMapper;
        this.watchlistRepository = watchlistRepository;
    }

    /**
     * Create a pharmacy and its associated watchlist.
     */
    @Transactional
    public PharmacyDTO create(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to create Pharmacy with Watchlist : {}", pharmacyDTO);

        if (pharmacyDTO.getName() == null || pharmacyDTO.getName().trim().isEmpty()) {
            throw new BadRequestAlertException("Pharmacy name is required", "pharmacy", "nameRequired");
        }

        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);
        pharmacy.setActive(true);
        pharmacy.setCreated(ZonedDateTime.now());
        pharmacy.setCreatedBy("admin");

        Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);

        // Create associated watchlist
        Watchlist watchlist = new Watchlist();
        watchlist.setName(savedPharmacy.getName() + " Watchlist");
        watchlist.setLimit(10);
        watchlist.setPharmacy(savedPharmacy); // This sets the ID via @MapsId

        watchlistRepository.save(watchlist);
        savedPharmacy.setWatchlist(watchlist);

        return pharmacyMapper.toDto(savedPharmacy);
    }

    /**
     * Update an existing pharmacy’s details.
     */
    public PharmacyDTO update(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to update Pharmacy : {}", pharmacyDTO);

        PharmacyDTO existingPharmacy = findOne(pharmacyDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Pharmacy not found", "pharmacy", "notfound"));

        if (pharmacyDTO.getName() != null && !pharmacyDTO.getName().trim().isEmpty()) {
            existingPharmacy.setName(pharmacyDTO.getName());
        }

        if (pharmacyDTO.getActive() != null && !Objects.equals(pharmacyDTO.getActive(), existingPharmacy.getActive())) {
            existingPharmacy.setActive(pharmacyDTO.getActive());
            if (pharmacyDTO.getActive()) {
                existingPharmacy.setActivatedBy("Admin");
            } else {
                existingPharmacy.setDeactivatedBy("Admin");
            }
        }

        existingPharmacy.setEmail(pharmacyDTO.getEmail());
        existingPharmacy.setAddress(pharmacyDTO.getAddress());
        existingPharmacy.setPhone(pharmacyDTO.getPhone());
        existingPharmacy.setWebsite(pharmacyDTO.getWebsite());

        return save(existingPharmacy);
    }

    /**
     * Save (create or update) a pharmacy.
     */
    public PharmacyDTO save(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to save Pharmacy : {}", pharmacyDTO);
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);
        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacyMapper.toDto(pharmacy);
    }

    /**
     * Get all pharmacies (paged).
     */
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pharmacies");
        return pharmacyRepository.findAll(pageable).map(pharmacyMapper::toDto);
    }

    /**
     * Get one pharmacy by ID.
     */
    @Transactional(readOnly = true)
    public Optional<PharmacyDTO> findOne(Long id) {
        LOG.debug("Request to get Pharmacy : {}", id);
        return pharmacyRepository.findById(id).map(pharmacyMapper::toDto);
    }

    /**
     * Delete a pharmacy by ID.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Pharmacy : {}", id);
        pharmacyRepository.deleteById(id);
    }
}
