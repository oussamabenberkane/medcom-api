package com.oussamabenberkane.medcom.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProductCriteriaTest {

    @Test
    void newProductCriteriaHasAllFiltersNullTest() {
        var productCriteria = new ProductCriteria();
        assertThat(productCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void productCriteriaFluentMethodsCreatesFiltersTest() {
        var productCriteria = new ProductCriteria();

        setAllFilters(productCriteria);

        assertThat(productCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void productCriteriaCopyCreatesNullFilterTest() {
        var productCriteria = new ProductCriteria();
        var copy = productCriteria.copy();

        assertThat(productCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(productCriteria)
        );
    }

    @Test
    void productCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var productCriteria = new ProductCriteria();
        setAllFilters(productCriteria);

        var copy = productCriteria.copy();

        assertThat(productCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(productCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var productCriteria = new ProductCriteria();

        assertThat(productCriteria).hasToString("ProductCriteria{}");
    }

    private static void setAllFilters(ProductCriteria productCriteria) {
        productCriteria.id();
        productCriteria.name();
        productCriteria.code();
        productCriteria.officialUrl();
        productCriteria.createdBy();
        productCriteria.updatedBy();
        productCriteria.distinct();
    }

    private static Condition<ProductCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getOfficialUrl()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProductCriteria> copyFiltersAre(ProductCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getOfficialUrl(), copy.getOfficialUrl()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
