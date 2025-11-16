package com.oussamabenberkane.medcom.domain;

import static com.oussamabenberkane.medcom.domain.PharmacyTestSamples.*;
import static com.oussamabenberkane.medcom.domain.WatchlistTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.oussamabenberkane.medcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WatchlistTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Watchlist.class);
        Watchlist watchlist1 = getWatchlistSample1();
        Watchlist watchlist2 = new Watchlist();
        assertThat(watchlist1).isNotEqualTo(watchlist2);

        watchlist2.setId(watchlist1.getId());
        assertThat(watchlist1).isEqualTo(watchlist2);

        watchlist2 = getWatchlistSample2();
        assertThat(watchlist1).isNotEqualTo(watchlist2);
    }

    @Test
    void pharmacyTest() {
        Watchlist watchlist = getWatchlistRandomSampleGenerator();
        Pharmacy pharmacyBack = getPharmacyRandomSampleGenerator();

        watchlist.setPharmacy(pharmacyBack);
        assertThat(watchlist.getPharmacy()).isEqualTo(pharmacyBack);

        watchlist.pharmacy(null);
        assertThat(watchlist.getPharmacy()).isNull();
    }
}
