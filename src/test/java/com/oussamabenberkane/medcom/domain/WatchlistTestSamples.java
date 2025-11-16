package com.oussamabenberkane.medcom.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WatchlistTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Watchlist getWatchlistSample1() {
        return new Watchlist().id(1L).createdBy("createdBy1");
    }

    public static Watchlist getWatchlistSample2() {
        return new Watchlist().id(2L).createdBy("createdBy2");
    }

    public static Watchlist getWatchlistRandomSampleGenerator() {
        return new Watchlist().id(longCount.incrementAndGet()).createdBy(UUID.randomUUID().toString());
    }
}
