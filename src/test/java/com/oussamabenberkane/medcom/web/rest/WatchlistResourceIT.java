package com.oussamabenberkane.medcom.web.rest;

import static com.oussamabenberkane.medcom.domain.WatchlistAsserts.*;
import static com.oussamabenberkane.medcom.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oussamabenberkane.medcom.IntegrationTest;
import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.repository.WatchlistRepository;
import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import com.oussamabenberkane.medcom.service.mapper.WatchlistMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WatchlistResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WatchlistResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/watchlists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WatchlistMapper watchlistMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWatchlistMockMvc;

    private Watchlist watchlist;

    private Watchlist insertedWatchlist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Watchlist createEntity(EntityManager em) {
        Watchlist watchlist = new Watchlist().createdAt(DEFAULT_CREATED_AT).createdBy(DEFAULT_CREATED_BY);
        // Add required entity
        Pharmacy pharmacy;
        if (TestUtil.findAll(em, Pharmacy.class).isEmpty()) {
            pharmacy = PharmacyResourceIT.createEntity();
            em.persist(pharmacy);
            em.flush();
        } else {
            pharmacy = TestUtil.findAll(em, Pharmacy.class).get(0);
        }
        watchlist.setPharmacy(pharmacy);
        return watchlist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Watchlist createUpdatedEntity(EntityManager em) {
        Watchlist updatedWatchlist = new Watchlist().createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);
        // Add required entity
        Pharmacy pharmacy;
        if (TestUtil.findAll(em, Pharmacy.class).isEmpty()) {
            pharmacy = PharmacyResourceIT.createUpdatedEntity();
            em.persist(pharmacy);
            em.flush();
        } else {
            pharmacy = TestUtil.findAll(em, Pharmacy.class).get(0);
        }
        updatedWatchlist.setPharmacy(pharmacy);
        return updatedWatchlist;
    }

    @BeforeEach
    void initTest() {
        watchlist = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWatchlist != null) {
            watchlistRepository.delete(insertedWatchlist);
            insertedWatchlist = null;
        }
    }

    @Test
    @Transactional
    void createWatchlist() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);
        var returnedWatchlistDTO = om.readValue(
            restWatchlistMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WatchlistDTO.class
        );

        // Validate the Watchlist in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWatchlist = watchlistMapper.toEntity(returnedWatchlistDTO);
        assertWatchlistUpdatableFieldsEquals(returnedWatchlist, getPersistedWatchlist(returnedWatchlist));

        insertedWatchlist = returnedWatchlist;
    }

    @Test
    @Transactional
    void createWatchlistWithExistingId() throws Exception {
        // Create the Watchlist with an existing ID
        watchlist.setId(1L);
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWatchlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWatchlists() throws Exception {
        // Initialize the database
        insertedWatchlist = watchlistRepository.saveAndFlush(watchlist);

        // Get all the watchlistList
        restWatchlistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(watchlist.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
    void getWatchlist() throws Exception {
        // Initialize the database
        insertedWatchlist = watchlistRepository.saveAndFlush(watchlist);

        // Get the watchlist
        restWatchlistMockMvc
            .perform(get(ENTITY_API_URL_ID, watchlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(watchlist.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingWatchlist() throws Exception {
        // Get the watchlist
        restWatchlistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWatchlist() throws Exception {
        // Initialize the database
        insertedWatchlist = watchlistRepository.saveAndFlush(watchlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the watchlist
        Watchlist updatedWatchlist = watchlistRepository.findById(watchlist.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWatchlist are not directly saved in db
        em.detach(updatedWatchlist);
        updatedWatchlist.createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(updatedWatchlist);

        restWatchlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, watchlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(watchlistDTO))
            )
            .andExpect(status().isOk());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWatchlistToMatchAllProperties(updatedWatchlist);
    }

    @Test
    @Transactional
    void putNonExistingWatchlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlist.setId(longCount.incrementAndGet());

        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWatchlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, watchlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(watchlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWatchlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlist.setId(longCount.incrementAndGet());

        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(watchlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWatchlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlist.setId(longCount.incrementAndGet());

        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWatchlistWithPatch() throws Exception {
        // Initialize the database
        insertedWatchlist = watchlistRepository.saveAndFlush(watchlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the watchlist using partial update
        Watchlist partialUpdatedWatchlist = new Watchlist();
        partialUpdatedWatchlist.setId(watchlist.getId());

        partialUpdatedWatchlist.createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);

        restWatchlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWatchlist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWatchlist))
            )
            .andExpect(status().isOk());

        // Validate the Watchlist in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWatchlistUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWatchlist, watchlist),
            getPersistedWatchlist(watchlist)
        );
    }

    @Test
    @Transactional
    void fullUpdateWatchlistWithPatch() throws Exception {
        // Initialize the database
        insertedWatchlist = watchlistRepository.saveAndFlush(watchlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the watchlist using partial update
        Watchlist partialUpdatedWatchlist = new Watchlist();
        partialUpdatedWatchlist.setId(watchlist.getId());

        partialUpdatedWatchlist.createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);

        restWatchlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWatchlist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWatchlist))
            )
            .andExpect(status().isOk());

        // Validate the Watchlist in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWatchlistUpdatableFieldsEquals(partialUpdatedWatchlist, getPersistedWatchlist(partialUpdatedWatchlist));
    }

    @Test
    @Transactional
    void patchNonExistingWatchlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlist.setId(longCount.incrementAndGet());

        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWatchlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, watchlistDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(watchlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWatchlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlist.setId(longCount.incrementAndGet());

        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(watchlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWatchlist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlist.setId(longCount.incrementAndGet());

        // Create the Watchlist
        WatchlistDTO watchlistDTO = watchlistMapper.toDto(watchlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(watchlistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Watchlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWatchlist() throws Exception {
        // Initialize the database
        insertedWatchlist = watchlistRepository.saveAndFlush(watchlist);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the watchlist
        restWatchlistMockMvc
            .perform(delete(ENTITY_API_URL_ID, watchlist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return watchlistRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Watchlist getPersistedWatchlist(Watchlist watchlist) {
        return watchlistRepository.findById(watchlist.getId()).orElseThrow();
    }

    protected void assertPersistedWatchlistToMatchAllProperties(Watchlist expectedWatchlist) {
        assertWatchlistAllPropertiesEquals(expectedWatchlist, getPersistedWatchlist(expectedWatchlist));
    }

    protected void assertPersistedWatchlistToMatchUpdatableProperties(Watchlist expectedWatchlist) {
        assertWatchlistAllUpdatablePropertiesEquals(expectedWatchlist, getPersistedWatchlist(expectedWatchlist));
    }
}
