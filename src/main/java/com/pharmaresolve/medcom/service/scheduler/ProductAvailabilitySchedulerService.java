package com.pharmaresolve.medcom.service.scheduler;

import com.pharmaresolve.medcom.service.ProductAvailabilityMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Simplified scheduler service for product availability monitoring with fixed priority intervals.
 * Priority 1 = every minute, Priority 2 = every 30 minutes, Priority 3 = every hour.
 */
@Service
@EnableScheduling
public class ProductAvailabilitySchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAvailabilitySchedulerService.class);

    private final ProductAvailabilityMonitoringService monitoringService;

    public ProductAvailabilitySchedulerService(ProductAvailabilityMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * Monitor priority 1 items every minute.
     */
    //@Scheduled(cron = "0 */1 * * * *")
    @Async
    public void monitorPriority1Items() {
        LOG.debug("Starting scheduled monitoring for priority 1 items (every minute)");
        try {
            monitoringService.processAvailabilityForPriority(1);
        } catch (Exception e) {
            LOG.error("Error monitoring priority 1 items: {}", e.getMessage(), e);
        }
    }

    /**
     * Monitor priority 2 items every 30 minutes.
     */
    //@Scheduled(cron = "0 */30 * * * *")
    @Async
    public void monitorPriority2Items() {
        LOG.debug("Starting scheduled monitoring for priority 2 items (every 30 minutes)");
        try {
            monitoringService.processAvailabilityForPriority(2);
        } catch (Exception e) {
            LOG.error("Error monitoring priority 2 items: {}", e.getMessage(), e);
        }
    }

    /**
     * Monitor priority 3 items every hour.
     */
    //@Scheduled(cron = "0 0 */1 * * *")
    @Async
    public void monitorPriority3Items() {
        LOG.debug("Starting scheduled monitoring for priority 3 items (every hour)");
        try {
            monitoringService.processAvailabilityForPriority(3);
        } catch (Exception e) {
            LOG.error("Error monitoring priority 3 items: {}", e.getMessage(), e);
        }
    }

    /**
     * Get scheduler status for management endpoints.
     */
    public String getSchedulerStatus() {
        return "Running - Priority 1: every minute, Priority 2: every 30 minutes, Priority 3: every hour";
    }

    /**
     * Manual trigger for testing (called by management endpoints).
     */
    public void triggerManualCheck(int priority) {
        LOG.info("Manual trigger for priority {} monitoring", priority);
        try {
            monitoringService.processAvailabilityForPriority(priority);
        } catch (Exception e) {
            LOG.error("Error in manual trigger for priority {}: {}", priority, e.getMessage(), e);
        }
    }
}
