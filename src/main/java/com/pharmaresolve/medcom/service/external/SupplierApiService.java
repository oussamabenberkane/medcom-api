package com.pharmaresolve.medcom.service.external;

import com.pharmaresolve.medcom.domain.Product;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * Service for checking product availability from external supplier API.
 */
@Service
public class SupplierApiService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierApiService.class);

    private final RestTemplate restTemplate;

    @Value("${medcom.supplier.api.base-url:https://api.supplier.example.com}")
    private String supplierApiBaseUrl;

    @Value("${medcom.supplier.api.timeout:5000}")
    private int timeout;

    public SupplierApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Check product availability from supplier API.
     *
     * @param product the product to check
     * @return availability status, empty if API call fails
     */
    public Optional<Boolean> checkProductAvailability(Product product) {
        LOG.debug("Checking availability for product: {} (ID: {})", product.getName(), product.getId());

        try {
            String endpoint = String.format("%s/products/%s/availability",
                supplierApiBaseUrl,
                product.getUniqueId() != null ? product.getUniqueId() : product.getId()
            );

            LOG.debug("Calling supplier API: {}", endpoint);

            // Placeholder API call - replace with actual supplier API structure
            ResponseEntity<SupplierAvailabilityResponse> response = restTemplate.getForEntity(
                endpoint,
                SupplierAvailabilityResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                boolean available = response.getBody().isAvailable();
                LOG.debug("Product {} availability: {}", product.getName(), available);
                return Optional.of(available);
            } else {
                LOG.warn("Supplier API returned non-success status: {} for product: {}",
                    response.getStatusCode(), product.getName());
                return Optional.empty();
            }

        } catch (RestClientException e) {
            LOG.error("Failed to check availability for product: {} - {}", product.getName(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            LOG.error("Unexpected error checking availability for product: {}", product.getName(), e);
            return Optional.empty();
        }
    }

    /**
     * Check if the supplier API is reachable.
     *
     * @return true if API is healthy, false otherwise
     */
    public boolean isApiHealthy() {
        try {
            String healthEndpoint = supplierApiBaseUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthEndpoint, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            LOG.warn("Supplier API health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * DTO for supplier API availability response.
     * This is a placeholder - replace with actual supplier API response structure.
     */
    public static class SupplierAvailabilityResponse {
        private boolean available;
        private String status;
        private Integer stockLevel;
        private String lastUpdated;

        // Constructors
        public SupplierAvailabilityResponse() {}

        public SupplierAvailabilityResponse(boolean available, String status) {
            this.available = available;
            this.status = status;
        }

        // Getters and setters
        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getStockLevel() {
            return stockLevel;
        }

        public void setStockLevel(Integer stockLevel) {
            this.stockLevel = stockLevel;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        @Override
        public String toString() {
            return "SupplierAvailabilityResponse{" +
                "available=" + available +
                ", status='" + status + '\'' +
                ", stockLevel=" + stockLevel +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
        }
    }
}