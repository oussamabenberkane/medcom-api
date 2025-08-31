package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Pharmacy;
import com.pharmaresolve.medcom.domain.Watchlist;
import com.pharmaresolve.medcom.repository.PharmacyRepository;
import com.pharmaresolve.medcom.repository.WatchlistRepository;
import com.pharmaresolve.medcom.service.dto.PharmacyDTO;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import com.pharmaresolve.medcom.service.mapper.PharmacyMapper;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
     * Create a new pharmacy with its associated watchlist.
     *
     * @param pharmacyDTO the pharmacy entity to create.
     * @return the persisted pharmacy entity with its watchlist.
     * @throws BadRequestAlertException if the pharmacy already has an ID.
     */
    @Transactional
    public PharmacyDTO create(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to create Pharmacy with Watchlist : {}", pharmacyDTO);

        if (pharmacyDTO.getName() == null || pharmacyDTO.getName().trim().isEmpty()) {
            throw new BadRequestAlertException("Pharmacy name is required", "pharmacy", "nameRequired");
        }

        // Convert DTO to entity and save pharmacy first
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);
        pharmacy.setActive(true);
        pharmacy.setCreated(ZonedDateTime.now());
        pharmacy.setCreatedBy("admin");

        // Save pharmacy to get the generated ID
        Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);

        // Now create watchlist with the pharmacy's ID
        Watchlist watchlist = new Watchlist();
        watchlist.setName(savedPharmacy.getName() + " Watchlist");
        watchlist.setLimit(10);
        watchlist.setPharmacy(savedPharmacy); // This sets the ID via @MapsId

        // Save watchlist
        watchlistRepository.save(watchlist);

        // Update the pharmacy's reference (optional, for consistency)
        savedPharmacy.setWatchlist(watchlist);

        return pharmacyMapper.toDto(savedPharmacy);
    }

    /**
     * Save a pharmacy.
     *
     * @param pharmacyDTO the entity to save.
     * @return the persisted entity.
     */
    public PharmacyDTO save(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to save Pharmacy : {}", pharmacyDTO);
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);
        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacyMapper.toDto(pharmacy);
    }

    /**
     * Update a pharmacy.
     *
     * @param pharmacyDTO the entity to save.
     * @return the persisted entity.
     */
    public PharmacyDTO update(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to update Pharmacy : {}", pharmacyDTO);
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);
        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacyMapper.toDto(pharmacy);
    }

    /**
     * Partially update a pharmacy.
     *
     * @param pharmacyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PharmacyDTO> partialUpdate(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to partially update Pharmacy : {}", pharmacyDTO);

        return pharmacyRepository
            .findById(pharmacyDTO.getId())
            .map(existingPharmacy -> {
                pharmacyMapper.partialUpdate(existingPharmacy, pharmacyDTO);

                return existingPharmacy;
            })
            .map(pharmacyRepository::save)
            .map(pharmacyMapper::toDto);
    }

    /**
     * Get all the pharmacies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pharmacies");
        return pharmacyRepository.findAll(pageable).map(pharmacyMapper::toDto);
    }

    /**
     *  Get all the pharmacies where Watchlist is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PharmacyDTO> findAllWhereWatchlistIsNull() {
        LOG.debug("Request to get all pharmacies where Watchlist is null");
        return StreamSupport.stream(pharmacyRepository.findAll().spliterator(), false)
            .filter(pharmacy -> pharmacy.getWatchlist() == null)
            .map(pharmacyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one pharmacy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PharmacyDTO> findOne(Long id) {
        LOG.debug("Request to get Pharmacy : {}", id);
        return pharmacyRepository.findById(id).map(pharmacyMapper::toDto);
    }

    /**
     * Delete the pharmacy by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Pharmacy : {}", id);
        pharmacyRepository.deleteById(id);
    }
}
