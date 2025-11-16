package com.oussamabenberkane.medcom.domain;

import static com.oussamabenberkane.medcom.domain.PharmacyTestSamples.*;
import static com.oussamabenberkane.medcom.domain.WatchlistTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.oussamabenberkane.medcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PharmacyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pharmacy.class);
        Pharmacy pharmacy1 = getPharmacySample1();
        Pharmacy pharmacy2 = new Pharmacy();
        assertThat(pharmacy1).isNotEqualTo(pharmacy2);

        pharmacy2.setId(pharmacy1.getId());
        assertThat(pharmacy1).isEqualTo(pharmacy2);

        pharmacy2 = getPharmacySample2();
        assertThat(pharmacy1).isNotEqualTo(pharmacy2);
    }

    @Test
    void watchlistTest() {
        Pharmacy pharmacy = getPharmacyRandomSampleGenerator();
        Watchlist watchlistBack = getWatchlistRandomSampleGenerator();

        pharmacy.setWatchlist(watchlistBack);
        assertThat(pharmacy.getWatchlist()).isEqualTo(watchlistBack);
        assertThat(watchlistBack.getPharmacy()).isEqualTo(pharmacy);

        pharmacy.watchlist(null);
        assertThat(pharmacy.getWatchlist()).isNull();
        assertThat(watchlistBack.getPharmacy()).isNull();
    }
}
