package com.oussamabenberkane.medcom.domain;

import static com.oussamabenberkane.medcom.domain.ProductTestSamples.*;
import static com.oussamabenberkane.medcom.domain.WatchlistItemTestSamples.*;
import static com.oussamabenberkane.medcom.domain.WatchlistTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.oussamabenberkane.medcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WatchlistItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WatchlistItem.class);
        WatchlistItem watchlistItem1 = getWatchlistItemSample1();
        WatchlistItem watchlistItem2 = new WatchlistItem();
        assertThat(watchlistItem1).isNotEqualTo(watchlistItem2);

        watchlistItem2.setId(watchlistItem1.getId());
        assertThat(watchlistItem1).isEqualTo(watchlistItem2);

        watchlistItem2 = getWatchlistItemSample2();
        assertThat(watchlistItem1).isNotEqualTo(watchlistItem2);
    }

    @Test
    void watchlistTest() {
        WatchlistItem watchlistItem = getWatchlistItemRandomSampleGenerator();
        Watchlist watchlistBack = getWatchlistRandomSampleGenerator();

        watchlistItem.setWatchlist(watchlistBack);
        assertThat(watchlistItem.getWatchlist()).isEqualTo(watchlistBack);

        watchlistItem.watchlist(null);
        assertThat(watchlistItem.getWatchlist()).isNull();
    }

    @Test
    void productTest() {
        WatchlistItem watchlistItem = getWatchlistItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        watchlistItem.setProduct(productBack);
        assertThat(watchlistItem.getProduct()).isEqualTo(productBack);

        watchlistItem.product(null);
        assertThat(watchlistItem.getProduct()).isNull();
    }
}
