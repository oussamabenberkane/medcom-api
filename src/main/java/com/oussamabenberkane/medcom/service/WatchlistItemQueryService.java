package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.domain.*; // for static metamodels
import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.repository.WatchlistItemRepository;
import com.oussamabenberkane.medcom.service.criteria.WatchlistItemCriteria;
import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import com.oussamabenberkane.medcom.service.mapper.WatchlistItemMapper;
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
 * Service for executing complex queries for {@link WatchlistItem} entities in the database.
 * The main input is a {@link WatchlistItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WatchlistItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WatchlistItemQueryService extends QueryService<WatchlistItem> {

    private static final Logger LOG = LoggerFactory.getLogger(WatchlistItemQueryService.class);

    private final WatchlistItemRepository watchlistItemRepository;

    private final WatchlistItemMapper watchlistItemMapper;

    public WatchlistItemQueryService(WatchlistItemRepository watchlistItemRepository, WatchlistItemMapper watchlistItemMapper) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.watchlistItemMapper = watchlistItemMapper;
    }

    /**
     * Return a {@link Page} of {@link WatchlistItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WatchlistItemDTO> findByCriteria(WatchlistItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WatchlistItem> specification = createSpecification(criteria);
        return watchlistItemRepository.findAll(specification, page).map(watchlistItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WatchlistItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WatchlistItem> specification = createSpecification(criteria);
        return watchlistItemRepository.count(specification);
    }

    /**
     * Function to convert {@link WatchlistItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WatchlistItem> createSpecification(WatchlistItemCriteria criteria) {
        Specification<WatchlistItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), WatchlistItem_.id),
                buildRangeSpecification(criteria.getDateAdded(), WatchlistItem_.dateAdded),
                buildStringSpecification(criteria.getAddedBy(), WatchlistItem_.addedBy),
                buildRangeSpecification(criteria.getDateUpdated(), WatchlistItem_.dateUpdated),
                buildStringSpecification(criteria.getUpdatedBy(), WatchlistItem_.updatedBy),
                buildSpecification(criteria.getLastAvailabilityStatus(), WatchlistItem_.lastAvailabilityStatus),
                buildRangeSpecification(criteria.getLastAvailabilityChange(), WatchlistItem_.lastAvailabilityChange),
                buildSpecification(criteria.getWatchlistId(), root -> root.join(WatchlistItem_.watchlist, JoinType.LEFT).get(Watchlist_.id)
                ),
                buildSpecification(criteria.getProductId(), root -> root.join(WatchlistItem_.product, JoinType.LEFT).get(Product_.id))
            );
        }
        return specification;
    }
}
