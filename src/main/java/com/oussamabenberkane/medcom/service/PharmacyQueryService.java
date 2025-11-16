package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.domain.*; // for static metamodels
import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.repository.PharmacyRepository;
import com.oussamabenberkane.medcom.service.criteria.PharmacyCriteria;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import com.oussamabenberkane.medcom.service.mapper.PharmacyMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Pharmacy} entities in the database.
 * The main input is a {@link PharmacyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PharmacyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PharmacyQueryService extends QueryService<Pharmacy> {

    private static final Logger LOG = LoggerFactory.getLogger(PharmacyQueryService.class);

    private final PharmacyRepository pharmacyRepository;

    private final PharmacyMapper pharmacyMapper;

    public PharmacyQueryService(PharmacyRepository pharmacyRepository, PharmacyMapper pharmacyMapper) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyMapper = pharmacyMapper;
    }

    /**
     * Return a {@link Page} of {@link PharmacyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> findByCriteria(PharmacyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pharmacy> specification = createSpecification(criteria);
        return pharmacyRepository.findAll(specification, page).map(pharmacyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PharmacyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Pharmacy> specification = createSpecification(criteria);
        return pharmacyRepository.count(specification);
    }

    /**
     * Function to convert {@link PharmacyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pharmacy> createSpecification(PharmacyCriteria criteria) {
        Specification<Pharmacy> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Pharmacy_.id),
                buildStringSpecification(criteria.getName(), Pharmacy_.name),
                buildStringSpecification(criteria.getAddress(), Pharmacy_.address),
                buildStringSpecification(criteria.getEmail(), Pharmacy_.email),
                buildStringSpecification(criteria.getPhone(), Pharmacy_.phone),
                buildStringSpecification(criteria.getWebsite(), Pharmacy_.website),
                buildSpecification(criteria.getActive(), Pharmacy_.active),
                buildStringSpecification(criteria.getActivatedBy(), Pharmacy_.activatedBy),
                buildStringSpecification(criteria.getDeactivatedBy(), Pharmacy_.deactivatedBy),
                buildSpecification(criteria.getDeleted(), Pharmacy_.deleted),
                buildStringSpecification(criteria.getDeletedBy(), Pharmacy_.deletedBy),
                buildSpecification(criteria.getWatchlistId(), root -> root.join(Pharmacy_.watchlist, JoinType.LEFT).get(Watchlist_.id))
            );
        }
        return specification;
    }
}
