package com.oussamabenberkane.medcom.service.scheduler;

import com.oussamabenberkane.medcom.service.ProductAvailabilityMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service for scheduling periodic product availability checks.
 */
@Service
@ConditionalOnProperty(value = "application.availability-monitoring.enabled", havingValue = "true", matchIfMissing = true)
public class ProductAvailabilitySchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAvailabilitySchedulerService.class);

    private final ProductAvailabilityMonitoringService monitoringService;

    public ProductAvailabilitySchedulerService(ProductAvailabilityMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * Scheduled job to check product availability.
     * Default interval: every minute (60000 ms).
     * Configurable via application.availability-monitoring.interval-ms property.
     */
    @Scheduled(fixedDelayString = "${application.availability-monitoring.interval-ms:60000}")
    public void checkProductAvailability() {
        LOG.debug("Scheduled availability check started");
        try {
            monitoringService.checkAllWatchlistItems();
        } catch (Exception e) {
            LOG.error("Error during scheduled availability check: {}", e.getMessage(), e);
        }
    }
}
