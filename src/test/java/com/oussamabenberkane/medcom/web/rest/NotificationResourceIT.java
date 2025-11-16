package com.oussamabenberkane.medcom.web.rest;

import static com.oussamabenberkane.medcom.domain.NotificationAsserts.*;
import static com.oussamabenberkane.medcom.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oussamabenberkane.medcom.IntegrationTest;
import com.oussamabenberkane.medcom.domain.Notification;
import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.domain.User;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.domain.enumeration.NotificationType;
import com.oussamabenberkane.medcom.repository.NotificationRepository;
import com.oussamabenberkane.medcom.repository.UserRepository;
import com.oussamabenberkane.medcom.service.dto.NotificationDTO;
import com.oussamabenberkane.medcom.service.mapper.NotificationMapper;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.AVAILABILITY_CHANGE;
    private static final NotificationType UPDATED_NOTIFICATION_TYPE = NotificationType.AVAILABILITY_CHANGE;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_EMAIL_SENT = false;
    private static final Boolean UPDATED_EMAIL_SENT = true;

    private static final Instant DEFAULT_EMAIL_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EMAIL_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_EMAIL_DELIVERED = false;
    private static final Boolean UPDATED_EMAIL_DELIVERED = true;

    private static final Instant DEFAULT_EMAIL_DELIVERED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EMAIL_DELIVERED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_EMAIL_FAILED = false;
    private static final Boolean UPDATED_EMAIL_FAILED = true;

    private static final Instant DEFAULT_EMAIL_FAILED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EMAIL_FAILED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_EMAIL_FAILURE_REASON = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_FAILURE_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_MAILJET_MESSAGE_ID = "AAAAAAAAAA";
    private static final String UPDATED_MAILJET_MESSAGE_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_READ_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_READ_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    private Notification notification;

    private Notification insertedNotification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity() {
        return new Notification()
            .createdAt(DEFAULT_CREATED_AT)
            .notificationType(DEFAULT_NOTIFICATION_TYPE)
            .message(DEFAULT_MESSAGE)
            .emailSent(DEFAULT_EMAIL_SENT)
            .emailSentAt(DEFAULT_EMAIL_SENT_AT)
            .emailDelivered(DEFAULT_EMAIL_DELIVERED)
            .emailDeliveredAt(DEFAULT_EMAIL_DELIVERED_AT)
            .emailFailed(DEFAULT_EMAIL_FAILED)
            .emailFailedAt(DEFAULT_EMAIL_FAILED_AT)
            .emailFailureReason(DEFAULT_EMAIL_FAILURE_REASON)
            .mailjetMessageId(DEFAULT_MAILJET_MESSAGE_ID)
            .readAt(DEFAULT_READ_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity() {
        return new Notification()
            .createdAt(UPDATED_CREATED_AT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .message(UPDATED_MESSAGE)
            .emailSent(UPDATED_EMAIL_SENT)
            .emailSentAt(UPDATED_EMAIL_SENT_AT)
            .emailDelivered(UPDATED_EMAIL_DELIVERED)
            .emailDeliveredAt(UPDATED_EMAIL_DELIVERED_AT)
            .emailFailed(UPDATED_EMAIL_FAILED)
            .emailFailedAt(UPDATED_EMAIL_FAILED_AT)
            .emailFailureReason(UPDATED_EMAIL_FAILURE_REASON)
            .mailjetMessageId(UPDATED_MAILJET_MESSAGE_ID)
            .readAt(UPDATED_READ_AT);
    }

    @BeforeEach
    void initTest() {
        notification = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotification != null) {
            notificationRepository.delete(insertedNotification);
            insertedNotification = null;
        }
    }

    @Test
    @Transactional
    void createNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        var returnedNotificationDTO = om.readValue(
            restNotificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationDTO.class
        );

        // Validate the Notification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotification = notificationMapper.toEntity(returnedNotificationDTO);
        assertNotificationUpdatableFieldsEquals(returnedNotification, getPersistedNotification(returnedNotification));

        insertedNotification = returnedNotification;
    }

    @Test
    @Transactional
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setCreatedAt(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotificationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setNotificationType(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotifications() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].emailSent").value(hasItem(DEFAULT_EMAIL_SENT)))
            .andExpect(jsonPath("$.[*].emailSentAt").value(hasItem(DEFAULT_EMAIL_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].emailDelivered").value(hasItem(DEFAULT_EMAIL_DELIVERED)))
            .andExpect(jsonPath("$.[*].emailDeliveredAt").value(hasItem(DEFAULT_EMAIL_DELIVERED_AT.toString())))
            .andExpect(jsonPath("$.[*].emailFailed").value(hasItem(DEFAULT_EMAIL_FAILED)))
            .andExpect(jsonPath("$.[*].emailFailedAt").value(hasItem(DEFAULT_EMAIL_FAILED_AT.toString())))
            .andExpect(jsonPath("$.[*].emailFailureReason").value(hasItem(DEFAULT_EMAIL_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].mailjetMessageId").value(hasItem(DEFAULT_MAILJET_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].readAt").value(hasItem(DEFAULT_READ_AT.toString())));
    }

    @Test
    @Transactional
    void getNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.notificationType").value(DEFAULT_NOTIFICATION_TYPE.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.emailSent").value(DEFAULT_EMAIL_SENT))
            .andExpect(jsonPath("$.emailSentAt").value(DEFAULT_EMAIL_SENT_AT.toString()))
            .andExpect(jsonPath("$.emailDelivered").value(DEFAULT_EMAIL_DELIVERED))
            .andExpect(jsonPath("$.emailDeliveredAt").value(DEFAULT_EMAIL_DELIVERED_AT.toString()))
            .andExpect(jsonPath("$.emailFailed").value(DEFAULT_EMAIL_FAILED))
            .andExpect(jsonPath("$.emailFailedAt").value(DEFAULT_EMAIL_FAILED_AT.toString()))
            .andExpect(jsonPath("$.emailFailureReason").value(DEFAULT_EMAIL_FAILURE_REASON))
            .andExpect(jsonPath("$.mailjetMessageId").value(DEFAULT_MAILJET_MESSAGE_ID))
            .andExpect(jsonPath("$.readAt").value(DEFAULT_READ_AT.toString()));
    }

    @Test
    @Transactional
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultNotificationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultNotificationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt equals to
        defaultNotificationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt in
        defaultNotificationFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt is not null
        defaultNotificationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationType equals to
        defaultNotificationFiltering(
            "notificationType.equals=" + DEFAULT_NOTIFICATION_TYPE,
            "notificationType.equals=" + UPDATED_NOTIFICATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationType in
        defaultNotificationFiltering(
            "notificationType.in=" + DEFAULT_NOTIFICATION_TYPE + "," + UPDATED_NOTIFICATION_TYPE,
            "notificationType.in=" + UPDATED_NOTIFICATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationType is not null
        defaultNotificationFiltering("notificationType.specified=true", "notificationType.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message equals to
        defaultNotificationFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message in
        defaultNotificationFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message is not null
        defaultNotificationFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message contains
        defaultNotificationFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message does not contain
        defaultNotificationFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailSentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailSent equals to
        defaultNotificationFiltering("emailSent.equals=" + DEFAULT_EMAIL_SENT, "emailSent.equals=" + UPDATED_EMAIL_SENT);
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailSentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailSent in
        defaultNotificationFiltering("emailSent.in=" + DEFAULT_EMAIL_SENT + "," + UPDATED_EMAIL_SENT, "emailSent.in=" + UPDATED_EMAIL_SENT);
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailSentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailSent is not null
        defaultNotificationFiltering("emailSent.specified=true", "emailSent.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailSentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailSentAt equals to
        defaultNotificationFiltering("emailSentAt.equals=" + DEFAULT_EMAIL_SENT_AT, "emailSentAt.equals=" + UPDATED_EMAIL_SENT_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailSentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailSentAt in
        defaultNotificationFiltering(
            "emailSentAt.in=" + DEFAULT_EMAIL_SENT_AT + "," + UPDATED_EMAIL_SENT_AT,
            "emailSentAt.in=" + UPDATED_EMAIL_SENT_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailSentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailSentAt is not null
        defaultNotificationFiltering("emailSentAt.specified=true", "emailSentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailDeliveredIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailDelivered equals to
        defaultNotificationFiltering(
            "emailDelivered.equals=" + DEFAULT_EMAIL_DELIVERED,
            "emailDelivered.equals=" + UPDATED_EMAIL_DELIVERED
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailDeliveredIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailDelivered in
        defaultNotificationFiltering(
            "emailDelivered.in=" + DEFAULT_EMAIL_DELIVERED + "," + UPDATED_EMAIL_DELIVERED,
            "emailDelivered.in=" + UPDATED_EMAIL_DELIVERED
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailDeliveredIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailDelivered is not null
        defaultNotificationFiltering("emailDelivered.specified=true", "emailDelivered.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailDeliveredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailDeliveredAt equals to
        defaultNotificationFiltering(
            "emailDeliveredAt.equals=" + DEFAULT_EMAIL_DELIVERED_AT,
            "emailDeliveredAt.equals=" + UPDATED_EMAIL_DELIVERED_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailDeliveredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailDeliveredAt in
        defaultNotificationFiltering(
            "emailDeliveredAt.in=" + DEFAULT_EMAIL_DELIVERED_AT + "," + UPDATED_EMAIL_DELIVERED_AT,
            "emailDeliveredAt.in=" + UPDATED_EMAIL_DELIVERED_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailDeliveredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailDeliveredAt is not null
        defaultNotificationFiltering("emailDeliveredAt.specified=true", "emailDeliveredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailed equals to
        defaultNotificationFiltering("emailFailed.equals=" + DEFAULT_EMAIL_FAILED, "emailFailed.equals=" + UPDATED_EMAIL_FAILED);
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailed in
        defaultNotificationFiltering(
            "emailFailed.in=" + DEFAULT_EMAIL_FAILED + "," + UPDATED_EMAIL_FAILED,
            "emailFailed.in=" + UPDATED_EMAIL_FAILED
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailed is not null
        defaultNotificationFiltering("emailFailed.specified=true", "emailFailed.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailedAt equals to
        defaultNotificationFiltering("emailFailedAt.equals=" + DEFAULT_EMAIL_FAILED_AT, "emailFailedAt.equals=" + UPDATED_EMAIL_FAILED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailedAt in
        defaultNotificationFiltering(
            "emailFailedAt.in=" + DEFAULT_EMAIL_FAILED_AT + "," + UPDATED_EMAIL_FAILED_AT,
            "emailFailedAt.in=" + UPDATED_EMAIL_FAILED_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailedAt is not null
        defaultNotificationFiltering("emailFailedAt.specified=true", "emailFailedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailureReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailureReason equals to
        defaultNotificationFiltering(
            "emailFailureReason.equals=" + DEFAULT_EMAIL_FAILURE_REASON,
            "emailFailureReason.equals=" + UPDATED_EMAIL_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailureReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailureReason in
        defaultNotificationFiltering(
            "emailFailureReason.in=" + DEFAULT_EMAIL_FAILURE_REASON + "," + UPDATED_EMAIL_FAILURE_REASON,
            "emailFailureReason.in=" + UPDATED_EMAIL_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailureReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailureReason is not null
        defaultNotificationFiltering("emailFailureReason.specified=true", "emailFailureReason.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailureReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailureReason contains
        defaultNotificationFiltering(
            "emailFailureReason.contains=" + DEFAULT_EMAIL_FAILURE_REASON,
            "emailFailureReason.contains=" + UPDATED_EMAIL_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByEmailFailureReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where emailFailureReason does not contain
        defaultNotificationFiltering(
            "emailFailureReason.doesNotContain=" + UPDATED_EMAIL_FAILURE_REASON,
            "emailFailureReason.doesNotContain=" + DEFAULT_EMAIL_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByMailjetMessageIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where mailjetMessageId equals to
        defaultNotificationFiltering(
            "mailjetMessageId.equals=" + DEFAULT_MAILJET_MESSAGE_ID,
            "mailjetMessageId.equals=" + UPDATED_MAILJET_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByMailjetMessageIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where mailjetMessageId in
        defaultNotificationFiltering(
            "mailjetMessageId.in=" + DEFAULT_MAILJET_MESSAGE_ID + "," + UPDATED_MAILJET_MESSAGE_ID,
            "mailjetMessageId.in=" + UPDATED_MAILJET_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByMailjetMessageIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where mailjetMessageId is not null
        defaultNotificationFiltering("mailjetMessageId.specified=true", "mailjetMessageId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByMailjetMessageIdContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where mailjetMessageId contains
        defaultNotificationFiltering(
            "mailjetMessageId.contains=" + DEFAULT_MAILJET_MESSAGE_ID,
            "mailjetMessageId.contains=" + UPDATED_MAILJET_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByMailjetMessageIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where mailjetMessageId does not contain
        defaultNotificationFiltering(
            "mailjetMessageId.doesNotContain=" + UPDATED_MAILJET_MESSAGE_ID,
            "mailjetMessageId.doesNotContain=" + DEFAULT_MAILJET_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByReadAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readAt equals to
        defaultNotificationFiltering("readAt.equals=" + DEFAULT_READ_AT, "readAt.equals=" + UPDATED_READ_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByReadAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readAt in
        defaultNotificationFiltering("readAt.in=" + DEFAULT_READ_AT + "," + UPDATED_READ_AT, "readAt.in=" + UPDATED_READ_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByReadAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readAt is not null
        defaultNotificationFiltering("readAt.specified=true", "readAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByWatchlistIsEqualToSomething() throws Exception {
        Watchlist watchlist;
        if (TestUtil.findAll(em, Watchlist.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            watchlist = WatchlistResourceIT.createEntity(em);
        } else {
            watchlist = TestUtil.findAll(em, Watchlist.class).get(0);
        }
        em.persist(watchlist);
        em.flush();
        notification.setWatchlist(watchlist);
        notificationRepository.saveAndFlush(notification);
        Long watchlistId = watchlist.getId();
        // Get all the notificationList where watchlist equals to watchlistId
        defaultNotificationShouldBeFound("watchlistId.equals=" + watchlistId);

        // Get all the notificationList where watchlist equals to (watchlistId + 1)
        defaultNotificationShouldNotBeFound("watchlistId.equals=" + (watchlistId + 1));
    }

    @Test
    @Transactional
    void getAllNotificationsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        notification.setUser(user);
        notificationRepository.saveAndFlush(notification);
        Long userId = user.getId();
        // Get all the notificationList where user equals to userId
        defaultNotificationShouldBeFound("userId.equals=" + userId);

        // Get all the notificationList where user equals to (userId + 1)
        defaultNotificationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllNotificationsByPharmacyIsEqualToSomething() throws Exception {
        Pharmacy pharmacy;
        if (TestUtil.findAll(em, Pharmacy.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            pharmacy = PharmacyResourceIT.createEntity();
        } else {
            pharmacy = TestUtil.findAll(em, Pharmacy.class).get(0);
        }
        em.persist(pharmacy);
        em.flush();
        notification.setPharmacy(pharmacy);
        notificationRepository.saveAndFlush(notification);
        Long pharmacyId = pharmacy.getId();
        // Get all the notificationList where pharmacy equals to pharmacyId
        defaultNotificationShouldBeFound("pharmacyId.equals=" + pharmacyId);

        // Get all the notificationList where pharmacy equals to (pharmacyId + 1)
        defaultNotificationShouldNotBeFound("pharmacyId.equals=" + (pharmacyId + 1));
    }

    @Test
    @Transactional
    void getAllNotificationsByWatchlistItemIsEqualToSomething() throws Exception {
        WatchlistItem watchlistItem;
        if (TestUtil.findAll(em, WatchlistItem.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            watchlistItem = WatchlistItemResourceIT.createEntity(em);
        } else {
            watchlistItem = TestUtil.findAll(em, WatchlistItem.class).get(0);
        }
        em.persist(watchlistItem);
        em.flush();
        notification.setWatchlistItem(watchlistItem);
        notificationRepository.saveAndFlush(notification);
        Long watchlistItemId = watchlistItem.getId();
        // Get all the notificationList where watchlistItem equals to watchlistItemId
        defaultNotificationShouldBeFound("watchlistItemId.equals=" + watchlistItemId);

        // Get all the notificationList where watchlistItem equals to (watchlistItemId + 1)
        defaultNotificationShouldNotBeFound("watchlistItemId.equals=" + (watchlistItemId + 1));
    }

    private void defaultNotificationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultNotificationShouldBeFound(shouldBeFound);
        defaultNotificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].emailSent").value(hasItem(DEFAULT_EMAIL_SENT)))
            .andExpect(jsonPath("$.[*].emailSentAt").value(hasItem(DEFAULT_EMAIL_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].emailDelivered").value(hasItem(DEFAULT_EMAIL_DELIVERED)))
            .andExpect(jsonPath("$.[*].emailDeliveredAt").value(hasItem(DEFAULT_EMAIL_DELIVERED_AT.toString())))
            .andExpect(jsonPath("$.[*].emailFailed").value(hasItem(DEFAULT_EMAIL_FAILED)))
            .andExpect(jsonPath("$.[*].emailFailedAt").value(hasItem(DEFAULT_EMAIL_FAILED_AT.toString())))
            .andExpect(jsonPath("$.[*].emailFailureReason").value(hasItem(DEFAULT_EMAIL_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].mailjetMessageId").value(hasItem(DEFAULT_MAILJET_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].readAt").value(hasItem(DEFAULT_READ_AT.toString())));

        // Check, that the count call also returns 1
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .createdAt(UPDATED_CREATED_AT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .message(UPDATED_MESSAGE)
            .emailSent(UPDATED_EMAIL_SENT)
            .emailSentAt(UPDATED_EMAIL_SENT_AT)
            .emailDelivered(UPDATED_EMAIL_DELIVERED)
            .emailDeliveredAt(UPDATED_EMAIL_DELIVERED_AT)
            .emailFailed(UPDATED_EMAIL_FAILED)
            .emailFailedAt(UPDATED_EMAIL_FAILED_AT)
            .emailFailureReason(UPDATED_EMAIL_FAILURE_REASON)
            .mailjetMessageId(UPDATED_MAILJET_MESSAGE_ID)
            .readAt(UPDATED_READ_AT);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationToMatchAllProperties(updatedNotification);
    }

    @Test
    @Transactional
    void putNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .emailSent(UPDATED_EMAIL_SENT)
            .emailDeliveredAt(UPDATED_EMAIL_DELIVERED_AT)
            .emailFailedAt(UPDATED_EMAIL_FAILED_AT)
            .emailFailureReason(UPDATED_EMAIL_FAILURE_REASON)
            .readAt(UPDATED_READ_AT);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotification, notification),
            getPersistedNotification(notification)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .createdAt(UPDATED_CREATED_AT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .message(UPDATED_MESSAGE)
            .emailSent(UPDATED_EMAIL_SENT)
            .emailSentAt(UPDATED_EMAIL_SENT_AT)
            .emailDelivered(UPDATED_EMAIL_DELIVERED)
            .emailDeliveredAt(UPDATED_EMAIL_DELIVERED_AT)
            .emailFailed(UPDATED_EMAIL_FAILED)
            .emailFailedAt(UPDATED_EMAIL_FAILED_AT)
            .emailFailureReason(UPDATED_EMAIL_FAILURE_REASON)
            .mailjetMessageId(UPDATED_MAILJET_MESSAGE_ID)
            .readAt(UPDATED_READ_AT);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(partialUpdatedNotification, getPersistedNotification(partialUpdatedNotification));
    }

    @Test
    @Transactional
    void patchNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationRepository.count();
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

    protected Notification getPersistedNotification(Notification notification) {
        return notificationRepository.findById(notification.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationToMatchAllProperties(Notification expectedNotification) {
        assertNotificationAllPropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }

    protected void assertPersistedNotificationToMatchUpdatableProperties(Notification expectedNotification) {
        assertNotificationAllUpdatablePropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }
}
