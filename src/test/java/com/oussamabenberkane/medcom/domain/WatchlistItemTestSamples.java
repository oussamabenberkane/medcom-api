package com.oussamabenberkane.medcom.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WatchlistItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static WatchlistItem getWatchlistItemSample1() {
        return new WatchlistItem().id(1L).addedBy("addedBy1").updatedBy("updatedBy1");
    }

    public static WatchlistItem getWatchlistItemSample2() {
        return new WatchlistItem().id(2L).addedBy("addedBy2").updatedBy("updatedBy2");
    }

    public static WatchlistItem getWatchlistItemRandomSampleGenerator() {
        return new WatchlistItem()
            .id(longCount.incrementAndGet())
            .addedBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
