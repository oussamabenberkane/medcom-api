package com.oussamabenberkane.medcom.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WatchlistItemCriteriaTest {

    @Test
    void newWatchlistItemCriteriaHasAllFiltersNullTest() {
        var watchlistItemCriteria = new WatchlistItemCriteria();
        assertThat(watchlistItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void watchlistItemCriteriaFluentMethodsCreatesFiltersTest() {
        var watchlistItemCriteria = new WatchlistItemCriteria();

        setAllFilters(watchlistItemCriteria);

        assertThat(watchlistItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void watchlistItemCriteriaCopyCreatesNullFilterTest() {
        var watchlistItemCriteria = new WatchlistItemCriteria();
        var copy = watchlistItemCriteria.copy();

        assertThat(watchlistItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(watchlistItemCriteria)
        );
    }

    @Test
    void watchlistItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var watchlistItemCriteria = new WatchlistItemCriteria();
        setAllFilters(watchlistItemCriteria);

        var copy = watchlistItemCriteria.copy();

        assertThat(watchlistItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(watchlistItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var watchlistItemCriteria = new WatchlistItemCriteria();

        assertThat(watchlistItemCriteria).hasToString("WatchlistItemCriteria{}");
    }

    private static void setAllFilters(WatchlistItemCriteria watchlistItemCriteria) {
        watchlistItemCriteria.id();
        watchlistItemCriteria.dateAdded();
        watchlistItemCriteria.addedBy();
        watchlistItemCriteria.dateUpdated();
        watchlistItemCriteria.updatedBy();
        watchlistItemCriteria.lastAvailabilityStatus();
        watchlistItemCriteria.lastAvailabilityChange();
        watchlistItemCriteria.watchlistId();
        watchlistItemCriteria.productId();
        watchlistItemCriteria.distinct();
    }

    private static Condition<WatchlistItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDateAdded()) &&
                condition.apply(criteria.getAddedBy()) &&
                condition.apply(criteria.getDateUpdated()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getLastAvailabilityStatus()) &&
                condition.apply(criteria.getLastAvailabilityChange()) &&
                condition.apply(criteria.getWatchlistId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WatchlistItemCriteria> copyFiltersAre(
        WatchlistItemCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDateAdded(), copy.getDateAdded()) &&
                condition.apply(criteria.getAddedBy(), copy.getAddedBy()) &&
                condition.apply(criteria.getDateUpdated(), copy.getDateUpdated()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getLastAvailabilityStatus(), copy.getLastAvailabilityStatus()) &&
                condition.apply(criteria.getLastAvailabilityChange(), copy.getLastAvailabilityChange()) &&
                condition.apply(criteria.getWatchlistId(), copy.getWatchlistId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
