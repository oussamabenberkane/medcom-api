package com.oussamabenberkane.medcom.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Medcom Api.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final SupplierApi supplierApi = new SupplierApi();
    private final AvailabilityMonitoring availabilityMonitoring = new AvailabilityMonitoring();
    private final Watchlist watchlist = new Watchlist();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public SupplierApi getSupplierApi() {
        return supplierApi;
    }

    public AvailabilityMonitoring getAvailabilityMonitoring() {
        return availabilityMonitoring;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class SupplierApi {

        private String baseUrl = "http://localhost:9000/api";
        private Boolean enabled = false;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class AvailabilityMonitoring {

        private Boolean enabled = true;
        private Long intervalMs = 60000L;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Long getIntervalMs() {
            return intervalMs;
        }

        public void setIntervalMs(Long intervalMs) {
            this.intervalMs = intervalMs;
        }
    }

    public static class Watchlist {

        private Integer maxItems = 10;

        public Integer getMaxItems() {
            return maxItems;
        }

        public void setMaxItems(Integer maxItems) {
            this.maxItems = maxItems;
        }
    }
    // jhipster-needle-application-properties-property-class
}
