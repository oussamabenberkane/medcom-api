package com.oussamabenberkane.medcom.service.impl;

import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.repository.PharmacyRepository;
import com.oussamabenberkane.medcom.security.AuthoritiesConstants;
import com.oussamabenberkane.medcom.security.SecurityUtils;
import com.oussamabenberkane.medcom.service.PharmacyService;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import com.oussamabenberkane.medcom.service.mapper.PharmacyMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.oussamabenberkane.medcom.domain.Pharmacy}.
 */
@Service
@Transactional
public class PharmacyServiceImpl implements PharmacyService {

    private static final Logger LOG = LoggerFactory.getLogger(PharmacyServiceImpl.class);

    private final PharmacyRepository pharmacyRepository;

    private final PharmacyMapper pharmacyMapper;

    public PharmacyServiceImpl(PharmacyRepository pharmacyRepository, PharmacyMapper pharmacyMapper) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyMapper = pharmacyMapper;
    }

    @Override
    public PharmacyDTO save(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to save Pharmacy : {}", pharmacyDTO);
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);

        // Auto-activate pharmacy when created by admin
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            pharmacy.setActive(true);
            String currentUser = SecurityUtils.getCurrentUserLogin().orElse("system");
            pharmacy.setActivatedBy(currentUser);
            LOG.info("Auto-activating pharmacy {} created by admin {}", pharmacy.getName(), currentUser);
        }

        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacyMapper.toDto(pharmacy);
    }

    @Override
    public PharmacyDTO update(PharmacyDTO pharmacyDTO) {
        LOG.debug("Request to update Pharmacy : {}", pharmacyDTO);
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDTO);
        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacyMapper.toDto(pharmacy);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Optional<PharmacyDTO> findOne(Long id) {
        LOG.debug("Request to get Pharmacy : {}", id);
        return pharmacyRepository.findById(id).map(pharmacyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Pharmacy : {}", id);
        pharmacyRepository.deleteById(id);
    }
}
