package com.oussamabenberkane.medcom.domain;

import static com.oussamabenberkane.medcom.domain.NotificationTestSamples.*;
import static com.oussamabenberkane.medcom.domain.PharmacyTestSamples.*;
import static com.oussamabenberkane.medcom.domain.WatchlistItemTestSamples.*;
import static com.oussamabenberkane.medcom.domain.WatchlistTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.oussamabenberkane.medcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void watchlistTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Watchlist watchlistBack = getWatchlistRandomSampleGenerator();

        notification.setWatchlist(watchlistBack);
        assertThat(notification.getWatchlist()).isEqualTo(watchlistBack);

        notification.watchlist(null);
        assertThat(notification.getWatchlist()).isNull();
    }

    @Test
    void pharmacyTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Pharmacy pharmacyBack = getPharmacyRandomSampleGenerator();

        notification.setPharmacy(pharmacyBack);
        assertThat(notification.getPharmacy()).isEqualTo(pharmacyBack);

        notification.pharmacy(null);
        assertThat(notification.getPharmacy()).isNull();
    }

    @Test
    void watchlistItemTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        WatchlistItem watchlistItemBack = getWatchlistItemRandomSampleGenerator();

        notification.setWatchlistItem(watchlistItemBack);
        assertThat(notification.getWatchlistItem()).isEqualTo(watchlistItemBack);

        notification.watchlistItem(null);
        assertThat(notification.getWatchlistItem()).isNull();
    }
}
