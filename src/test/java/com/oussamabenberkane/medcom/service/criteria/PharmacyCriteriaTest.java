package com.oussamabenberkane.medcom.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PharmacyCriteriaTest {

    @Test
    void newPharmacyCriteriaHasAllFiltersNullTest() {
        var pharmacyCriteria = new PharmacyCriteria();
        assertThat(pharmacyCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void pharmacyCriteriaFluentMethodsCreatesFiltersTest() {
        var pharmacyCriteria = new PharmacyCriteria();

        setAllFilters(pharmacyCriteria);

        assertThat(pharmacyCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void pharmacyCriteriaCopyCreatesNullFilterTest() {
        var pharmacyCriteria = new PharmacyCriteria();
        var copy = pharmacyCriteria.copy();

        assertThat(pharmacyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(pharmacyCriteria)
        );
    }

    @Test
    void pharmacyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var pharmacyCriteria = new PharmacyCriteria();
        setAllFilters(pharmacyCriteria);

        var copy = pharmacyCriteria.copy();

        assertThat(pharmacyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(pharmacyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var pharmacyCriteria = new PharmacyCriteria();

        assertThat(pharmacyCriteria).hasToString("PharmacyCriteria{}");
    }

    private static void setAllFilters(PharmacyCriteria pharmacyCriteria) {
        pharmacyCriteria.id();
        pharmacyCriteria.name();
        pharmacyCriteria.address();
        pharmacyCriteria.email();
        pharmacyCriteria.phone();
        pharmacyCriteria.website();
        pharmacyCriteria.active();
        pharmacyCriteria.activatedBy();
        pharmacyCriteria.deactivatedBy();
        pharmacyCriteria.deleted();
        pharmacyCriteria.deletedBy();
        pharmacyCriteria.watchlistId();
        pharmacyCriteria.distinct();
    }

    private static Condition<PharmacyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getWebsite()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getActivatedBy()) &&
                condition.apply(criteria.getDeactivatedBy()) &&
                condition.apply(criteria.getDeleted()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getWatchlistId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PharmacyCriteria> copyFiltersAre(PharmacyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getWebsite(), copy.getWebsite()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getActivatedBy(), copy.getActivatedBy()) &&
                condition.apply(criteria.getDeactivatedBy(), copy.getDeactivatedBy()) &&
                condition.apply(criteria.getDeleted(), copy.getDeleted()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getWatchlistId(), copy.getWatchlistId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
