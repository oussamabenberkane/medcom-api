package com.oussamabenberkane.medcom.service.external;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for checking product availability from external supplier API.
 */
@Service
public class SupplierApiService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierApiService.class);

    private final RestTemplate restTemplate;

    @Value("${application.supplier-api.base-url:http://localhost:9000/api}")
    private String supplierApiBaseUrl;

    @Value("${application.supplier-api.enabled:false}")
    private boolean supplierApiEnabled;

    public SupplierApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Check product availability from supplier API.
     *
     * @param productCode The unique product code used for API calls
     * @return Map containing "available" boolean attribute
     */
    public Map<String, Object> checkProductAvailability(String productCode) {
        Map<String, Object> result = new HashMap<>();

        if (!supplierApiEnabled) {
            LOG.debug("Supplier API is disabled, returning placeholder data for product: {}", productCode);
            // Placeholder: simulate availability check
            result.put("available", Math.random() > 0.5); // Random availability for testing
            result.put("placeholder", true);
            return result;
        }

        try {
            String url = supplierApiBaseUrl + "/products/" + productCode + "/availability";
            LOG.debug("Checking availability for product {} at URL: {}", productCode, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                result = response.getBody();
                LOG.debug("Successfully retrieved availability for product {}: {}", productCode, result.get("available"));
            } else {
                LOG.warn("Unexpected response from supplier API for product {}: {}", productCode, response.getStatusCode());
                result.put("available", false);
                result.put("error", "Unexpected response from supplier API");
            }
        } catch (RestClientException e) {
            LOG.error("Error checking availability for product {}: {}", productCode, e.getMessage());
            result.put("available", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * Check if the supplier API is available and responding.
     *
     * @return true if API is reachable, false otherwise
     */
    public boolean isSupplierApiAvailable() {
        if (!supplierApiEnabled) {
            return false;
        }

        try {
            String url = supplierApiBaseUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            LOG.warn("Supplier API health check failed: {}", e.getMessage());
            return false;
        }
    }
}
