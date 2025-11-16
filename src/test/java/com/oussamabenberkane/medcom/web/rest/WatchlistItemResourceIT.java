package com.oussamabenberkane.medcom.web.rest;

import static com.oussamabenberkane.medcom.domain.WatchlistItemAsserts.*;
import static com.oussamabenberkane.medcom.web.rest.TestUtil.createUpdateProxyForBean;
import static com.oussamabenberkane.medcom.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oussamabenberkane.medcom.IntegrationTest;
import com.oussamabenberkane.medcom.domain.Product;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.repository.WatchlistItemRepository;
import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import com.oussamabenberkane.medcom.service.mapper.WatchlistItemMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link WatchlistItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WatchlistItemResourceIT {

    private static final Instant DEFAULT_DATE_ADDED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ADDED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ADDED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_LAST_AVAILABILITY_STATUS = false;
    private static final Boolean UPDATED_LAST_AVAILABILITY_STATUS = true;

    private static final ZonedDateTime DEFAULT_LAST_AVAILABILITY_CHANGE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_AVAILABILITY_CHANGE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_AVAILABILITY_CHANGE = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(-1L),
        ZoneOffset.UTC
    );

    private static final String ENTITY_API_URL = "/api/watchlist-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WatchlistItemRepository watchlistItemRepository;

    @Autowired
    private WatchlistItemMapper watchlistItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWatchlistItemMockMvc;

    private WatchlistItem watchlistItem;

    private WatchlistItem insertedWatchlistItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WatchlistItem createEntity(EntityManager em) {
        WatchlistItem watchlistItem = new WatchlistItem()
            .dateAdded(DEFAULT_DATE_ADDED)
            .addedBy(DEFAULT_ADDED_BY)
            .dateUpdated(DEFAULT_DATE_UPDATED)
            .updatedBy(DEFAULT_UPDATED_BY)
            .lastAvailabilityStatus(DEFAULT_LAST_AVAILABILITY_STATUS)
            .lastAvailabilityChange(DEFAULT_LAST_AVAILABILITY_CHANGE);
        // Add required entity
        Watchlist watchlist;
        if (TestUtil.findAll(em, Watchlist.class).isEmpty()) {
            watchlist = WatchlistResourceIT.createEntity(em);
            em.persist(watchlist);
            em.flush();
        } else {
            watchlist = TestUtil.findAll(em, Watchlist.class).get(0);
        }
        watchlistItem.setWatchlist(watchlist);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity();
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        watchlistItem.setProduct(product);
        return watchlistItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WatchlistItem createUpdatedEntity(EntityManager em) {
        WatchlistItem updatedWatchlistItem = new WatchlistItem()
            .dateAdded(UPDATED_DATE_ADDED)
            .addedBy(UPDATED_ADDED_BY)
            .dateUpdated(UPDATED_DATE_UPDATED)
            .updatedBy(UPDATED_UPDATED_BY)
            .lastAvailabilityStatus(UPDATED_LAST_AVAILABILITY_STATUS)
            .lastAvailabilityChange(UPDATED_LAST_AVAILABILITY_CHANGE);
        // Add required entity
        Watchlist watchlist;
        if (TestUtil.findAll(em, Watchlist.class).isEmpty()) {
            watchlist = WatchlistResourceIT.createUpdatedEntity(em);
            em.persist(watchlist);
            em.flush();
        } else {
            watchlist = TestUtil.findAll(em, Watchlist.class).get(0);
        }
        updatedWatchlistItem.setWatchlist(watchlist);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity();
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        updatedWatchlistItem.setProduct(product);
        return updatedWatchlistItem;
    }

    @BeforeEach
    void initTest() {
        watchlistItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWatchlistItem != null) {
            watchlistItemRepository.delete(insertedWatchlistItem);
            insertedWatchlistItem = null;
        }
    }

    @Test
    @Transactional
    void createWatchlistItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);
        var returnedWatchlistItemDTO = om.readValue(
            restWatchlistItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WatchlistItemDTO.class
        );

        // Validate the WatchlistItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWatchlistItem = watchlistItemMapper.toEntity(returnedWatchlistItemDTO);
        assertWatchlistItemUpdatableFieldsEquals(returnedWatchlistItem, getPersistedWatchlistItem(returnedWatchlistItem));

        insertedWatchlistItem = returnedWatchlistItem;
    }

    @Test
    @Transactional
    void createWatchlistItemWithExistingId() throws Exception {
        // Create the WatchlistItem with an existing ID
        watchlistItem.setId(1L);
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWatchlistItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateAddedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        watchlistItem.setDateAdded(null);

        // Create the WatchlistItem, which fails.
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        restWatchlistItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWatchlistItems() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList
        restWatchlistItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(watchlistItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].addedBy").value(hasItem(DEFAULT_ADDED_BY)))
            .andExpect(jsonPath("$.[*].dateUpdated").value(hasItem(DEFAULT_DATE_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].lastAvailabilityStatus").value(hasItem(DEFAULT_LAST_AVAILABILITY_STATUS)))
            .andExpect(jsonPath("$.[*].lastAvailabilityChange").value(hasItem(sameInstant(DEFAULT_LAST_AVAILABILITY_CHANGE))));
    }

    @Test
    @Transactional
    void getWatchlistItem() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get the watchlistItem
        restWatchlistItemMockMvc
            .perform(get(ENTITY_API_URL_ID, watchlistItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(watchlistItem.getId().intValue()))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()))
            .andExpect(jsonPath("$.addedBy").value(DEFAULT_ADDED_BY))
            .andExpect(jsonPath("$.dateUpdated").value(DEFAULT_DATE_UPDATED.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.lastAvailabilityStatus").value(DEFAULT_LAST_AVAILABILITY_STATUS))
            .andExpect(jsonPath("$.lastAvailabilityChange").value(sameInstant(DEFAULT_LAST_AVAILABILITY_CHANGE)));
    }

    @Test
    @Transactional
    void getWatchlistItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        Long id = watchlistItem.getId();

        defaultWatchlistItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWatchlistItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWatchlistItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByDateAddedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where dateAdded equals to
        defaultWatchlistItemFiltering("dateAdded.equals=" + DEFAULT_DATE_ADDED, "dateAdded.equals=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByDateAddedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where dateAdded in
        defaultWatchlistItemFiltering(
            "dateAdded.in=" + DEFAULT_DATE_ADDED + "," + UPDATED_DATE_ADDED,
            "dateAdded.in=" + UPDATED_DATE_ADDED
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByDateAddedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where dateAdded is not null
        defaultWatchlistItemFiltering("dateAdded.specified=true", "dateAdded.specified=false");
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByAddedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where addedBy equals to
        defaultWatchlistItemFiltering("addedBy.equals=" + DEFAULT_ADDED_BY, "addedBy.equals=" + UPDATED_ADDED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByAddedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where addedBy in
        defaultWatchlistItemFiltering("addedBy.in=" + DEFAULT_ADDED_BY + "," + UPDATED_ADDED_BY, "addedBy.in=" + UPDATED_ADDED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByAddedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where addedBy is not null
        defaultWatchlistItemFiltering("addedBy.specified=true", "addedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByAddedByContainsSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where addedBy contains
        defaultWatchlistItemFiltering("addedBy.contains=" + DEFAULT_ADDED_BY, "addedBy.contains=" + UPDATED_ADDED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByAddedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where addedBy does not contain
        defaultWatchlistItemFiltering("addedBy.doesNotContain=" + UPDATED_ADDED_BY, "addedBy.doesNotContain=" + DEFAULT_ADDED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByDateUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where dateUpdated equals to
        defaultWatchlistItemFiltering("dateUpdated.equals=" + DEFAULT_DATE_UPDATED, "dateUpdated.equals=" + UPDATED_DATE_UPDATED);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByDateUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where dateUpdated in
        defaultWatchlistItemFiltering(
            "dateUpdated.in=" + DEFAULT_DATE_UPDATED + "," + UPDATED_DATE_UPDATED,
            "dateUpdated.in=" + UPDATED_DATE_UPDATED
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByDateUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where dateUpdated is not null
        defaultWatchlistItemFiltering("dateUpdated.specified=true", "dateUpdated.specified=false");
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where updatedBy equals to
        defaultWatchlistItemFiltering("updatedBy.equals=" + DEFAULT_UPDATED_BY, "updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where updatedBy in
        defaultWatchlistItemFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where updatedBy is not null
        defaultWatchlistItemFiltering("updatedBy.specified=true", "updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where updatedBy contains
        defaultWatchlistItemFiltering("updatedBy.contains=" + DEFAULT_UPDATED_BY, "updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where updatedBy does not contain
        defaultWatchlistItemFiltering("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY, "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityStatus equals to
        defaultWatchlistItemFiltering(
            "lastAvailabilityStatus.equals=" + DEFAULT_LAST_AVAILABILITY_STATUS,
            "lastAvailabilityStatus.equals=" + UPDATED_LAST_AVAILABILITY_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityStatus in
        defaultWatchlistItemFiltering(
            "lastAvailabilityStatus.in=" + DEFAULT_LAST_AVAILABILITY_STATUS + "," + UPDATED_LAST_AVAILABILITY_STATUS,
            "lastAvailabilityStatus.in=" + UPDATED_LAST_AVAILABILITY_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityStatus is not null
        defaultWatchlistItemFiltering("lastAvailabilityStatus.specified=true", "lastAvailabilityStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange equals to
        defaultWatchlistItemFiltering(
            "lastAvailabilityChange.equals=" + DEFAULT_LAST_AVAILABILITY_CHANGE,
            "lastAvailabilityChange.equals=" + UPDATED_LAST_AVAILABILITY_CHANGE
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange in
        defaultWatchlistItemFiltering(
            "lastAvailabilityChange.in=" + DEFAULT_LAST_AVAILABILITY_CHANGE + "," + UPDATED_LAST_AVAILABILITY_CHANGE,
            "lastAvailabilityChange.in=" + UPDATED_LAST_AVAILABILITY_CHANGE
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange is not null
        defaultWatchlistItemFiltering("lastAvailabilityChange.specified=true", "lastAvailabilityChange.specified=false");
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange is greater than or equal to
        defaultWatchlistItemFiltering(
            "lastAvailabilityChange.greaterThanOrEqual=" + DEFAULT_LAST_AVAILABILITY_CHANGE,
            "lastAvailabilityChange.greaterThanOrEqual=" + UPDATED_LAST_AVAILABILITY_CHANGE
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange is less than or equal to
        defaultWatchlistItemFiltering(
            "lastAvailabilityChange.lessThanOrEqual=" + DEFAULT_LAST_AVAILABILITY_CHANGE,
            "lastAvailabilityChange.lessThanOrEqual=" + SMALLER_LAST_AVAILABILITY_CHANGE
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange is less than
        defaultWatchlistItemFiltering(
            "lastAvailabilityChange.lessThan=" + UPDATED_LAST_AVAILABILITY_CHANGE,
            "lastAvailabilityChange.lessThan=" + DEFAULT_LAST_AVAILABILITY_CHANGE
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByLastAvailabilityChangeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        // Get all the watchlistItemList where lastAvailabilityChange is greater than
        defaultWatchlistItemFiltering(
            "lastAvailabilityChange.greaterThan=" + SMALLER_LAST_AVAILABILITY_CHANGE,
            "lastAvailabilityChange.greaterThan=" + DEFAULT_LAST_AVAILABILITY_CHANGE
        );
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByWatchlistIsEqualToSomething() throws Exception {
        Watchlist watchlist;
        if (TestUtil.findAll(em, Watchlist.class).isEmpty()) {
            watchlistItemRepository.saveAndFlush(watchlistItem);
            watchlist = WatchlistResourceIT.createEntity(em);
        } else {
            watchlist = TestUtil.findAll(em, Watchlist.class).get(0);
        }
        em.persist(watchlist);
        em.flush();
        watchlistItem.setWatchlist(watchlist);
        watchlistItemRepository.saveAndFlush(watchlistItem);
        Long watchlistId = watchlist.getId();
        // Get all the watchlistItemList where watchlist equals to watchlistId
        defaultWatchlistItemShouldBeFound("watchlistId.equals=" + watchlistId);

        // Get all the watchlistItemList where watchlist equals to (watchlistId + 1)
        defaultWatchlistItemShouldNotBeFound("watchlistId.equals=" + (watchlistId + 1));
    }

    @Test
    @Transactional
    void getAllWatchlistItemsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            watchlistItemRepository.saveAndFlush(watchlistItem);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        watchlistItem.setProduct(product);
        watchlistItemRepository.saveAndFlush(watchlistItem);
        Long productId = product.getId();
        // Get all the watchlistItemList where product equals to productId
        defaultWatchlistItemShouldBeFound("productId.equals=" + productId);

        // Get all the watchlistItemList where product equals to (productId + 1)
        defaultWatchlistItemShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultWatchlistItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWatchlistItemShouldBeFound(shouldBeFound);
        defaultWatchlistItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWatchlistItemShouldBeFound(String filter) throws Exception {
        restWatchlistItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(watchlistItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].addedBy").value(hasItem(DEFAULT_ADDED_BY)))
            .andExpect(jsonPath("$.[*].dateUpdated").value(hasItem(DEFAULT_DATE_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].lastAvailabilityStatus").value(hasItem(DEFAULT_LAST_AVAILABILITY_STATUS)))
            .andExpect(jsonPath("$.[*].lastAvailabilityChange").value(hasItem(sameInstant(DEFAULT_LAST_AVAILABILITY_CHANGE))));

        // Check, that the count call also returns 1
        restWatchlistItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWatchlistItemShouldNotBeFound(String filter) throws Exception {
        restWatchlistItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWatchlistItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWatchlistItem() throws Exception {
        // Get the watchlistItem
        restWatchlistItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWatchlistItem() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the watchlistItem
        WatchlistItem updatedWatchlistItem = watchlistItemRepository.findById(watchlistItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWatchlistItem are not directly saved in db
        em.detach(updatedWatchlistItem);
        updatedWatchlistItem
            .dateAdded(UPDATED_DATE_ADDED)
            .addedBy(UPDATED_ADDED_BY)
            .dateUpdated(UPDATED_DATE_UPDATED)
            .updatedBy(UPDATED_UPDATED_BY)
            .lastAvailabilityStatus(UPDATED_LAST_AVAILABILITY_STATUS)
            .lastAvailabilityChange(UPDATED_LAST_AVAILABILITY_CHANGE);
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(updatedWatchlistItem);

        restWatchlistItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, watchlistItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(watchlistItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWatchlistItemToMatchAllProperties(updatedWatchlistItem);
    }

    @Test
    @Transactional
    void putNonExistingWatchlistItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlistItem.setId(longCount.incrementAndGet());

        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWatchlistItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, watchlistItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(watchlistItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWatchlistItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlistItem.setId(longCount.incrementAndGet());

        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(watchlistItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWatchlistItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlistItem.setId(longCount.incrementAndGet());

        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(watchlistItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWatchlistItemWithPatch() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the watchlistItem using partial update
        WatchlistItem partialUpdatedWatchlistItem = new WatchlistItem();
        partialUpdatedWatchlistItem.setId(watchlistItem.getId());

        partialUpdatedWatchlistItem.updatedBy(UPDATED_UPDATED_BY).lastAvailabilityChange(UPDATED_LAST_AVAILABILITY_CHANGE);

        restWatchlistItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWatchlistItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWatchlistItem))
            )
            .andExpect(status().isOk());

        // Validate the WatchlistItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWatchlistItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWatchlistItem, watchlistItem),
            getPersistedWatchlistItem(watchlistItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateWatchlistItemWithPatch() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the watchlistItem using partial update
        WatchlistItem partialUpdatedWatchlistItem = new WatchlistItem();
        partialUpdatedWatchlistItem.setId(watchlistItem.getId());

        partialUpdatedWatchlistItem
            .dateAdded(UPDATED_DATE_ADDED)
            .addedBy(UPDATED_ADDED_BY)
            .dateUpdated(UPDATED_DATE_UPDATED)
            .updatedBy(UPDATED_UPDATED_BY)
            .lastAvailabilityStatus(UPDATED_LAST_AVAILABILITY_STATUS)
            .lastAvailabilityChange(UPDATED_LAST_AVAILABILITY_CHANGE);

        restWatchlistItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWatchlistItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWatchlistItem))
            )
            .andExpect(status().isOk());

        // Validate the WatchlistItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWatchlistItemUpdatableFieldsEquals(partialUpdatedWatchlistItem, getPersistedWatchlistItem(partialUpdatedWatchlistItem));
    }

    @Test
    @Transactional
    void patchNonExistingWatchlistItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlistItem.setId(longCount.incrementAndGet());

        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWatchlistItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, watchlistItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(watchlistItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWatchlistItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlistItem.setId(longCount.incrementAndGet());

        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(watchlistItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWatchlistItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        watchlistItem.setId(longCount.incrementAndGet());

        // Create the WatchlistItem
        WatchlistItemDTO watchlistItemDTO = watchlistItemMapper.toDto(watchlistItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWatchlistItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(watchlistItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WatchlistItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWatchlistItem() throws Exception {
        // Initialize the database
        insertedWatchlistItem = watchlistItemRepository.saveAndFlush(watchlistItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the watchlistItem
        restWatchlistItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, watchlistItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return watchlistItemRepository.count();
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

    protected WatchlistItem getPersistedWatchlistItem(WatchlistItem watchlistItem) {
        return watchlistItemRepository.findById(watchlistItem.getId()).orElseThrow();
    }

    protected void assertPersistedWatchlistItemToMatchAllProperties(WatchlistItem expectedWatchlistItem) {
        assertWatchlistItemAllPropertiesEquals(expectedWatchlistItem, getPersistedWatchlistItem(expectedWatchlistItem));
    }

    protected void assertPersistedWatchlistItemToMatchUpdatableProperties(WatchlistItem expectedWatchlistItem) {
        assertWatchlistItemAllUpdatablePropertiesEquals(expectedWatchlistItem, getPersistedWatchlistItem(expectedWatchlistItem));
    }
}
