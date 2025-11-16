package com.oussamabenberkane.medcom.web.rest;

import static com.oussamabenberkane.medcom.domain.PharmacyAsserts.*;
import static com.oussamabenberkane.medcom.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oussamabenberkane.medcom.IntegrationTest;
import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.repository.PharmacyRepository;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import com.oussamabenberkane.medcom.service.mapper.PharmacyMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PharmacyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PharmacyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "?KywS@NfXWR.2}>?ro";
    private static final String UPDATED_EMAIL = ",:@,.EB,";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_ACTIVATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_DEACTIVATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DEACTIVATED_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String DEFAULT_DELETED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DELETED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pharmacies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private PharmacyMapper pharmacyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPharmacyMockMvc;

    private Pharmacy pharmacy;

    private Pharmacy insertedPharmacy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pharmacy createEntity() {
        return new Pharmacy()
            .name(DEFAULT_NAME)
            .address(DEFAULT_ADDRESS)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .website(DEFAULT_WEBSITE)
            .active(DEFAULT_ACTIVE)
            .activatedBy(DEFAULT_ACTIVATED_BY)
            .deactivatedBy(DEFAULT_DEACTIVATED_BY)
            .deleted(DEFAULT_DELETED)
            .deletedBy(DEFAULT_DELETED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pharmacy createUpdatedEntity() {
        return new Pharmacy()
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .website(UPDATED_WEBSITE)
            .active(UPDATED_ACTIVE)
            .activatedBy(UPDATED_ACTIVATED_BY)
            .deactivatedBy(UPDATED_DEACTIVATED_BY)
            .deleted(UPDATED_DELETED)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        pharmacy = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPharmacy != null) {
            pharmacyRepository.delete(insertedPharmacy);
            insertedPharmacy = null;
        }
    }

    @Test
    @Transactional
    void createPharmacy() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);
        var returnedPharmacyDTO = om.readValue(
            restPharmacyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pharmacyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PharmacyDTO.class
        );

        // Validate the Pharmacy in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPharmacy = pharmacyMapper.toEntity(returnedPharmacyDTO);
        assertPharmacyUpdatableFieldsEquals(returnedPharmacy, getPersistedPharmacy(returnedPharmacy));

        insertedPharmacy = returnedPharmacy;
    }

    @Test
    @Transactional
    void createPharmacyWithExistingId() throws Exception {
        // Create the Pharmacy with an existing ID
        pharmacy.setId(1L);
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPharmacyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pharmacyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pharmacy.setName(null);

        // Create the Pharmacy, which fails.
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        restPharmacyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pharmacyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pharmacy.setEmail(null);

        // Create the Pharmacy, which fails.
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        restPharmacyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pharmacyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pharmacy.setActive(null);

        // Create the Pharmacy, which fails.
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        restPharmacyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pharmacyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPharmacies() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList
        restPharmacyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pharmacy.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].activatedBy").value(hasItem(DEFAULT_ACTIVATED_BY)))
            .andExpect(jsonPath("$.[*].deactivatedBy").value(hasItem(DEFAULT_DEACTIVATED_BY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY)));
    }

    @Test
    @Transactional
    void getPharmacy() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get the pharmacy
        restPharmacyMockMvc
            .perform(get(ENTITY_API_URL_ID, pharmacy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pharmacy.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.activatedBy").value(DEFAULT_ACTIVATED_BY))
            .andExpect(jsonPath("$.deactivatedBy").value(DEFAULT_DEACTIVATED_BY))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY));
    }

    @Test
    @Transactional
    void getPharmaciesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        Long id = pharmacy.getId();

        defaultPharmacyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPharmacyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPharmacyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPharmaciesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where name equals to
        defaultPharmacyFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPharmaciesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where name in
        defaultPharmacyFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPharmaciesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where name is not null
        defaultPharmacyFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where name contains
        defaultPharmacyFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPharmaciesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where name does not contain
        defaultPharmacyFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPharmaciesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where address equals to
        defaultPharmacyFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPharmaciesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where address in
        defaultPharmacyFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPharmaciesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where address is not null
        defaultPharmacyFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where address contains
        defaultPharmacyFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPharmaciesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where address does not contain
        defaultPharmacyFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPharmaciesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where email equals to
        defaultPharmacyFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPharmaciesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where email in
        defaultPharmacyFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPharmaciesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where email is not null
        defaultPharmacyFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where email contains
        defaultPharmacyFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPharmaciesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where email does not contain
        defaultPharmacyFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllPharmaciesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where phone equals to
        defaultPharmacyFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where phone in
        defaultPharmacyFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where phone is not null
        defaultPharmacyFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where phone contains
        defaultPharmacyFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where phone does not contain
        defaultPharmacyFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByWebsiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where website equals to
        defaultPharmacyFiltering("website.equals=" + DEFAULT_WEBSITE, "website.equals=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByWebsiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where website in
        defaultPharmacyFiltering("website.in=" + DEFAULT_WEBSITE + "," + UPDATED_WEBSITE, "website.in=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByWebsiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where website is not null
        defaultPharmacyFiltering("website.specified=true", "website.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByWebsiteContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where website contains
        defaultPharmacyFiltering("website.contains=" + DEFAULT_WEBSITE, "website.contains=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByWebsiteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where website does not contain
        defaultPharmacyFiltering("website.doesNotContain=" + UPDATED_WEBSITE, "website.doesNotContain=" + DEFAULT_WEBSITE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where active equals to
        defaultPharmacyFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where active in
        defaultPharmacyFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPharmaciesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where active is not null
        defaultPharmacyFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByActivatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where activatedBy equals to
        defaultPharmacyFiltering("activatedBy.equals=" + DEFAULT_ACTIVATED_BY, "activatedBy.equals=" + UPDATED_ACTIVATED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByActivatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where activatedBy in
        defaultPharmacyFiltering(
            "activatedBy.in=" + DEFAULT_ACTIVATED_BY + "," + UPDATED_ACTIVATED_BY,
            "activatedBy.in=" + UPDATED_ACTIVATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPharmaciesByActivatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where activatedBy is not null
        defaultPharmacyFiltering("activatedBy.specified=true", "activatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByActivatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where activatedBy contains
        defaultPharmacyFiltering("activatedBy.contains=" + DEFAULT_ACTIVATED_BY, "activatedBy.contains=" + UPDATED_ACTIVATED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByActivatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where activatedBy does not contain
        defaultPharmacyFiltering(
            "activatedBy.doesNotContain=" + UPDATED_ACTIVATED_BY,
            "activatedBy.doesNotContain=" + DEFAULT_ACTIVATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeactivatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deactivatedBy equals to
        defaultPharmacyFiltering("deactivatedBy.equals=" + DEFAULT_DEACTIVATED_BY, "deactivatedBy.equals=" + UPDATED_DEACTIVATED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeactivatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deactivatedBy in
        defaultPharmacyFiltering(
            "deactivatedBy.in=" + DEFAULT_DEACTIVATED_BY + "," + UPDATED_DEACTIVATED_BY,
            "deactivatedBy.in=" + UPDATED_DEACTIVATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeactivatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deactivatedBy is not null
        defaultPharmacyFiltering("deactivatedBy.specified=true", "deactivatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeactivatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deactivatedBy contains
        defaultPharmacyFiltering("deactivatedBy.contains=" + DEFAULT_DEACTIVATED_BY, "deactivatedBy.contains=" + UPDATED_DEACTIVATED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeactivatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deactivatedBy does not contain
        defaultPharmacyFiltering(
            "deactivatedBy.doesNotContain=" + UPDATED_DEACTIVATED_BY,
            "deactivatedBy.doesNotContain=" + DEFAULT_DEACTIVATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deleted equals to
        defaultPharmacyFiltering("deleted.equals=" + DEFAULT_DELETED, "deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deleted in
        defaultPharmacyFiltering("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED, "deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deleted is not null
        defaultPharmacyFiltering("deleted.specified=true", "deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deletedBy equals to
        defaultPharmacyFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deletedBy in
        defaultPharmacyFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deletedBy is not null
        defaultPharmacyFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedByContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deletedBy contains
        defaultPharmacyFiltering("deletedBy.contains=" + DEFAULT_DELETED_BY, "deletedBy.contains=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllPharmaciesByDeletedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        // Get all the pharmacyList where deletedBy does not contain
        defaultPharmacyFiltering("deletedBy.doesNotContain=" + UPDATED_DELETED_BY, "deletedBy.doesNotContain=" + DEFAULT_DELETED_BY);
    }

    private void defaultPharmacyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPharmacyShouldBeFound(shouldBeFound);
        defaultPharmacyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPharmacyShouldBeFound(String filter) throws Exception {
        restPharmacyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pharmacy.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].activatedBy").value(hasItem(DEFAULT_ACTIVATED_BY)))
            .andExpect(jsonPath("$.[*].deactivatedBy").value(hasItem(DEFAULT_DEACTIVATED_BY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY)));

        // Check, that the count call also returns 1
        restPharmacyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPharmacyShouldNotBeFound(String filter) throws Exception {
        restPharmacyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPharmacyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPharmacy() throws Exception {
        // Get the pharmacy
        restPharmacyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPharmacy() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pharmacy
        Pharmacy updatedPharmacy = pharmacyRepository.findById(pharmacy.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPharmacy are not directly saved in db
        em.detach(updatedPharmacy);
        updatedPharmacy
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .website(UPDATED_WEBSITE)
            .active(UPDATED_ACTIVE)
            .activatedBy(UPDATED_ACTIVATED_BY)
            .deactivatedBy(UPDATED_DEACTIVATED_BY)
            .deleted(UPDATED_DELETED)
            .deletedBy(UPDATED_DELETED_BY);
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(updatedPharmacy);

        restPharmacyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pharmacyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pharmacyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPharmacyToMatchAllProperties(updatedPharmacy);
    }

    @Test
    @Transactional
    void putNonExistingPharmacy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pharmacy.setId(longCount.incrementAndGet());

        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPharmacyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pharmacyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pharmacyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPharmacy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pharmacy.setId(longCount.incrementAndGet());

        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPharmacyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pharmacyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPharmacy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pharmacy.setId(longCount.incrementAndGet());

        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPharmacyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pharmacyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePharmacyWithPatch() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pharmacy using partial update
        Pharmacy partialUpdatedPharmacy = new Pharmacy();
        partialUpdatedPharmacy.setId(pharmacy.getId());

        partialUpdatedPharmacy.phone(UPDATED_PHONE).activatedBy(UPDATED_ACTIVATED_BY).deactivatedBy(UPDATED_DEACTIVATED_BY);

        restPharmacyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPharmacy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPharmacy))
            )
            .andExpect(status().isOk());

        // Validate the Pharmacy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPharmacyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPharmacy, pharmacy), getPersistedPharmacy(pharmacy));
    }

    @Test
    @Transactional
    void fullUpdatePharmacyWithPatch() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pharmacy using partial update
        Pharmacy partialUpdatedPharmacy = new Pharmacy();
        partialUpdatedPharmacy.setId(pharmacy.getId());

        partialUpdatedPharmacy
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .website(UPDATED_WEBSITE)
            .active(UPDATED_ACTIVE)
            .activatedBy(UPDATED_ACTIVATED_BY)
            .deactivatedBy(UPDATED_DEACTIVATED_BY)
            .deleted(UPDATED_DELETED)
            .deletedBy(UPDATED_DELETED_BY);

        restPharmacyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPharmacy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPharmacy))
            )
            .andExpect(status().isOk());

        // Validate the Pharmacy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPharmacyUpdatableFieldsEquals(partialUpdatedPharmacy, getPersistedPharmacy(partialUpdatedPharmacy));
    }

    @Test
    @Transactional
    void patchNonExistingPharmacy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pharmacy.setId(longCount.incrementAndGet());

        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPharmacyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pharmacyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pharmacyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPharmacy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pharmacy.setId(longCount.incrementAndGet());

        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPharmacyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pharmacyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPharmacy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pharmacy.setId(longCount.incrementAndGet());

        // Create the Pharmacy
        PharmacyDTO pharmacyDTO = pharmacyMapper.toDto(pharmacy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPharmacyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pharmacyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pharmacy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePharmacy() throws Exception {
        // Initialize the database
        insertedPharmacy = pharmacyRepository.saveAndFlush(pharmacy);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pharmacy
        restPharmacyMockMvc
            .perform(delete(ENTITY_API_URL_ID, pharmacy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pharmacyRepository.count();
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

    protected Pharmacy getPersistedPharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.findById(pharmacy.getId()).orElseThrow();
    }

    protected void assertPersistedPharmacyToMatchAllProperties(Pharmacy expectedPharmacy) {
        assertPharmacyAllPropertiesEquals(expectedPharmacy, getPersistedPharmacy(expectedPharmacy));
    }

    protected void assertPersistedPharmacyToMatchUpdatableProperties(Pharmacy expectedPharmacy) {
        assertPharmacyAllUpdatablePropertiesEquals(expectedPharmacy, getPersistedPharmacy(expectedPharmacy));
    }
}
