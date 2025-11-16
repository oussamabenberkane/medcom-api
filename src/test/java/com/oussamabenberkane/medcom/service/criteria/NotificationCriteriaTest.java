package com.oussamabenberkane.medcom.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class NotificationCriteriaTest {

    @Test
    void newNotificationCriteriaHasAllFiltersNullTest() {
        var notificationCriteria = new NotificationCriteria();
        assertThat(notificationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void notificationCriteriaFluentMethodsCreatesFiltersTest() {
        var notificationCriteria = new NotificationCriteria();

        setAllFilters(notificationCriteria);

        assertThat(notificationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void notificationCriteriaCopyCreatesNullFilterTest() {
        var notificationCriteria = new NotificationCriteria();
        var copy = notificationCriteria.copy();

        assertThat(notificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationCriteria)
        );
    }

    @Test
    void notificationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var notificationCriteria = new NotificationCriteria();
        setAllFilters(notificationCriteria);

        var copy = notificationCriteria.copy();

        assertThat(notificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var notificationCriteria = new NotificationCriteria();

        assertThat(notificationCriteria).hasToString("NotificationCriteria{}");
    }

    private static void setAllFilters(NotificationCriteria notificationCriteria) {
        notificationCriteria.id();
        notificationCriteria.createdAt();
        notificationCriteria.notificationType();
        notificationCriteria.message();
        notificationCriteria.emailSent();
        notificationCriteria.emailSentAt();
        notificationCriteria.emailDelivered();
        notificationCriteria.emailDeliveredAt();
        notificationCriteria.emailFailed();
        notificationCriteria.emailFailedAt();
        notificationCriteria.emailFailureReason();
        notificationCriteria.mailjetMessageId();
        notificationCriteria.readAt();
        notificationCriteria.watchlistId();
        notificationCriteria.userId();
        notificationCriteria.pharmacyId();
        notificationCriteria.watchlistItemId();
        notificationCriteria.distinct();
    }

    private static Condition<NotificationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getNotificationType()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getEmailSent()) &&
                condition.apply(criteria.getEmailSentAt()) &&
                condition.apply(criteria.getEmailDelivered()) &&
                condition.apply(criteria.getEmailDeliveredAt()) &&
                condition.apply(criteria.getEmailFailed()) &&
                condition.apply(criteria.getEmailFailedAt()) &&
                condition.apply(criteria.getEmailFailureReason()) &&
                condition.apply(criteria.getMailjetMessageId()) &&
                condition.apply(criteria.getReadAt()) &&
                condition.apply(criteria.getWatchlistId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getPharmacyId()) &&
                condition.apply(criteria.getWatchlistItemId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<NotificationCriteria> copyFiltersAre(
        NotificationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getNotificationType(), copy.getNotificationType()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getEmailSent(), copy.getEmailSent()) &&
                condition.apply(criteria.getEmailSentAt(), copy.getEmailSentAt()) &&
                condition.apply(criteria.getEmailDelivered(), copy.getEmailDelivered()) &&
                condition.apply(criteria.getEmailDeliveredAt(), copy.getEmailDeliveredAt()) &&
                condition.apply(criteria.getEmailFailed(), copy.getEmailFailed()) &&
                condition.apply(criteria.getEmailFailedAt(), copy.getEmailFailedAt()) &&
                condition.apply(criteria.getEmailFailureReason(), copy.getEmailFailureReason()) &&
                condition.apply(criteria.getMailjetMessageId(), copy.getMailjetMessageId()) &&
                condition.apply(criteria.getReadAt(), copy.getReadAt()) &&
                condition.apply(criteria.getWatchlistId(), copy.getWatchlistId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getPharmacyId(), copy.getPharmacyId()) &&
                condition.apply(criteria.getWatchlistItemId(), copy.getWatchlistItemId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
